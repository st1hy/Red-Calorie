package com.github.st1hy.countthemcalories.core.adapter;

import android.database.Cursor;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public abstract class RxDaoSearchAdapter<T extends RecyclerView.ViewHolder> extends CursorRecyclerViewAdapter<T>
        implements DaoRecyclerAdapter {

    public static int debounceTime = 250;
    final SearchableDatabase db;

    protected String lastQuery = "";

    public RxDaoSearchAdapter(@NonNull SearchableDatabase db) {
        this.db = db;
    }

    @Override
    @CallSuper
    public void onSearch(@NonNull Observable<CharSequence> observable) {
        Observable<CharSequence> sequenceObservable = observable
                .subscribeOn(AndroidSchedulers.mainThread());
        if (debounceTime > 0) {
            sequenceObservable = sequenceObservable.share();
            sequenceObservable = sequenceObservable
                    .limit(1)
                    .concatWith(
                            sequenceObservable
                                    .skip(1)
                                    .debounce(debounceTime, TimeUnit.MILLISECONDS)
                    );
        }
        addSubscription(sequenceObservable
                .doOnNext(text -> Timber.v("Search notification: queryText='%s'", text))
                .map(CharSequence::toString)
                .flatMap(query -> {
                    Observable<Cursor> cursor11 = getAllWithFilter(query);
                    return cursor11.map(cursor1 -> Pair.create(query, cursor1));
                })
                .doOnError(e -> Timber.e(e, "Search exploded"))
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    Timber.v("Db cursor query ended");
                    lastQuery = result.first;
                    onCursorUpdate(result.first, result.second);
                    notifyDataSetChanged();
                }));
    }

    @CallSuper
    protected void onCursorUpdate(@NonNull String query, @NonNull Cursor cursor) {
        super.onCursorUpdate(cursor);
    }

    @NonNull
    @CheckResult
    protected Observable<Cursor> getAllWithFilter(@NonNull String filter) {
        return db.getAllFiltered(filter);
    }

}
