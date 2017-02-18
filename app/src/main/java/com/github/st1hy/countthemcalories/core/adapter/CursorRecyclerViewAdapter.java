package com.github.st1hy.countthemcalories.core.adapter;

import android.database.Cursor;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    final CompositeSubscription subscriptions = new CompositeSubscription();
    //Lazy
    private Subject<RecyclerEvent, RecyclerEvent> changeEvents;

    private Cursor cursor;

    @CallSuper
    public void onStart() {

    }

    @CallSuper
    public void onStop() {
        subscriptions.clear();
        closeCursor(true);
    }

    @Override
    public int getItemCount() {
        return getDaoItemCount();
    }

    @CallSuper
    protected void onCursorUpdate(@NonNull Cursor cursor) {
        closeCursor(false);
        CursorRecyclerViewAdapter.this.cursor = cursor;
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

    protected void notifyListenersItemRemove(int position) {
        getEventSubject().onNext(RecyclerEvent.create(RecyclerEvent.Type.REMOVED, position));
    }

    protected void notifyListenersItemInserted(int position) {
        getEventSubject().onNext(RecyclerEvent.create(RecyclerEvent.Type.ADDED, position));
    }
}
