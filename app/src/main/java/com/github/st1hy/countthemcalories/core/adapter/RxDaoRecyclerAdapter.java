package com.github.st1hy.countthemcalories.core.adapter;

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
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public abstract class RxDaoRecyclerAdapter<T extends RecyclerView.ViewHolder, R> extends RecyclerView.Adapter<T>
        implements DaoRecyclerAdapter {

    public static int debounceTime = 250;
    final RxDatabaseModel<R> databaseModel;

    final CompositeSubscription subscriptions = new CompositeSubscription();
    //Lazy
    private Subject<RecyclerEvent, RecyclerEvent> changeEvents;

    Cursor cursor;
    Observable<CharSequence> onSearchObservable;

    public RxDaoRecyclerAdapter(@NonNull RxDatabaseModel<R> databaseModel) {
        this.databaseModel = databaseModel;
    }

    @Override
    @CallSuper
    public void onStart() {
        if (onSearchObservable != null) onSearch(onSearchObservable);
    }

    @Override
    @CallSuper
    public void onStop() {
        subscriptions.clear();
        closeCursor(true);
    }

    @Override
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
        addSubscription(sequenceObservable
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
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OnCursor() {
                    @Override
                    public void onNext(Cursor cursor) {
                        super.onNext(cursor);
                        notifyDataSetChanged();
                    }
                }));
    }

    @Override
    public int getItemCount() {
        return getDaoItemCount();
    }

    @CallSuper
    protected void onCursorUpdate(@NonNull Cursor cursor) {
        closeCursor(false);
        RxDaoRecyclerAdapter.this.cursor = cursor;
    }

    @NonNull
    private Func1<? super CharSequence, ? extends Observable<Cursor>> queryDatabaseFiltered() {
        return new Func1<CharSequence, Observable<Cursor>>() {
            @Override
            public Observable<Cursor> call(CharSequence text) {
                return getAllWithFilter(text.toString());
            }
        };
    }

    @NonNull
    protected Observable<Cursor> getAllWithFilter(@NonNull String filter) {
        return databaseModel.getAllFiltered(filter);
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
    protected void addSubscription(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }

    @NonNull
    protected Subject<RecyclerEvent, RecyclerEvent> getEventSubject() {
        if (changeEvents == null) {
            changeEvents = PublishSubject.<RecyclerEvent>create().toSerialized();
        }
        return changeEvents;
    }

    public class OnCursor extends Subscriber<Cursor> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "Error when providing cursor");
        }

        @Override
        @CallSuper
        public void onNext(Cursor cursor) {
            Timber.v("Db cursor query ended");
            onCursorUpdate(cursor);
        }
    }
}
