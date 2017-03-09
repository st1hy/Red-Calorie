package com.github.st1hy.countthemcalories.core.adapter;

import android.database.Cursor;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;

import rx.subjects.PublishSubject;

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    @Inject
    PublishSubject<RecyclerEvent> changeEvents;

    private Cursor cursor;

    @Override
    public int getItemCount() {
        return getDaoItemCount();
    }

    @CallSuper
    protected void onCursorUpdate(@NonNull Cursor cursor) {
        closeCursor(false);
        CursorRecyclerViewAdapter.this.cursor = cursor;
    }

    public void closeCursor(boolean notify) {
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

    protected void notifyListenersItemRemove(int position) {
        changeEvents.onNext(RecyclerEvent.create(RecyclerEvent.Type.REMOVED, position));
    }

    protected void notifyListenersItemInserted(int position) {
        changeEvents.onNext(RecyclerEvent.create(RecyclerEvent.Type.ADDED, position));
    }
}
