package com.github.st1hy.countthemcalories.core.adapter;

import android.database.Cursor;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public abstract class RxDaoSearchAdapter<T extends RecyclerView.ViewHolder, R> extends RecyclerView.Adapter<T>
        implements DaoRecyclerAdapter {

    public static int debounceTime = 250;
    final SearchableDatabase db;

    final CompositeSubscription subscriptions = new CompositeSubscription();
    //Lazy
    private Subject<RecyclerEvent, RecyclerEvent> changeEvents;

    Cursor cursor;
    Observable<CharSequence> onSearchObservable;

    public RxDaoSearchAdapter(@NonNull SearchableDatabase db) {
        this.db = db;
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
                .subscribe(new SimpleSubscriber<Cursor>() {
                    @Override
                    public void onNext(Cursor cursor) {
                        Timber.v("Db cursor query ended");
                        onCursorUpdate(cursor);
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
        RxDaoSearchAdapter.this.cursor = cursor;
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
        return db.getAllFiltered(filter);
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

    /**
     * Same as {@link #notifyItemRemoved(int)} but also notifies event subject
     */
    protected void notifyItemRemovedRx(int position) {
        getEventSubject().onNext(RecyclerEvent.create(RecyclerEvent.Type.REMOVED, position));
        notifyItemRemoved(position);
    }

    /**
     * Same as {@link #notifyItemInserted(int)} but also notifies event subject
     */
    protected void notifyItemInsertedRx(int position) {
        getEventSubject().onNext(RecyclerEvent.create(RecyclerEvent.Type.ADDED, position));
        notifyItemInserted(position);
    }
}
