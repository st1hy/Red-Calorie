package com.github.st1hy.countthemcalories.core.presenter;

import android.database.Cursor;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public abstract class RxDaoRecyclerAdapter<T extends RecyclerView.ViewHolder, R> extends RecyclerView.Adapter<T> {

    public static int debounceTime = 250;
    final RxDatabaseModel<R> databaseModel;

    final CompositeSubscription subscriptions = new CompositeSubscription();

    Cursor cursor;
    Observable<CharSequence> onSearchObservable;

    public RxDaoRecyclerAdapter(@NonNull RxDatabaseModel<R> databaseModel) {
        this.databaseModel = databaseModel;
    }

    @CallSuper
    public void onStart() {
        if (onSearchObservable != null) onSearch(onSearchObservable);
    }

    @CallSuper
    public void onStop() {
        subscriptions.clear();
        closeCursor(true);
    }

    @CallSuper
    public void onSearch(@NonNull Observable<CharSequence> observable) {
        onSearchObservable = observable;
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
        Observable<Cursor> cursorObservable = sequenceObservable
                .doOnNext(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence text) {
                        Timber.v("Search notification: queryText='%s'", text);
                    }
                })
                .flatMap(queryDatabaseFiltered())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        Timber.e(e, "Search exploded");
                    }
                })
                .retry();
        subscribeToCursor(cursorObservable);
    }

    @Override
    public int getItemCount() {
        return getDaoItemCount();
    }

    @CallSuper
    protected final void subscribeToCursor(@NonNull Observable<Cursor> cursorObservable) {
        subscriptions.add(cursorObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Cursor>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error when providing cursor");
                    }

                    @Override
                    public void onNext(Cursor cursor) {
                        onCursorUpdate(cursor);
                    }
                }));
    }


    @CallSuper
    protected void onCursorUpdate(@NonNull Cursor cursor) {
        Timber.v("Db cursor query ended");
        closeCursor(false);
        RxDaoRecyclerAdapter.this.cursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    private Func1<? super CharSequence, ? extends Observable<Cursor>> queryDatabaseFiltered() {
        return new Func1<CharSequence, Observable<Cursor>>() {
            @Override
            public Observable<Cursor> call(CharSequence text) {
                return databaseModel.getAllFiltered(text.toString());
            }
        };
    }

    private void closeCursor(boolean notify) {
        Cursor cursor = this.cursor;
        this.cursor = null;
        if (cursor != null) {
            if (notify) notifyDataSetChanged();
            cursor.close();
        }
    }

    protected final int getDaoItemCount() {
        Cursor cursor = getCursor();
        return cursor != null ? cursor.getCount() : 0;
    }

    protected Cursor getCursor() {
        return cursor;
    }

    @CallSuper
    protected void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }
}
