package com.github.st1hy.countthemcalories.core.adapter.delegate;

import org.greenrobot.greendao.annotation.NotNull;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewNotification.Type.*;
import static com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewNotification.of;

public class RecyclerViewNotifierObservable implements RecyclerViewNotifier {

    @NotNull
    private final PublishSubject<RecyclerViewNotification> notificationPublishSubject = PublishSubject.create();

    @NotNull
    public Observable<RecyclerViewNotification> asObservable() {
        return notificationPublishSubject.asObservable();
    }

    @Override
    public void notifyDataSetChanged() {
        notificationPublishSubject.onNext(of(DATA_SET_CHANGED, -1, -1, null));
    }

    @Override
    public void notifyItemChanged(int position) {
        notifyItemChanged(position, null);
    }

    @Override
    public void notifyItemChanged(int position, Object payload) {
        notificationPublishSubject.onNext(of(ITEM_CHANGED, position, -1, payload));
    }

    @Override
    public void notifyItemRangeChanged(int positionStart, int itemCount) {
        notifyItemRangeChanged(positionStart, itemCount, null);
    }

    @Override
    public void notifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
        notificationPublishSubject.onNext(of(RANGE_CHANGED, positionStart, itemCount, payload));
    }

    @Override
    public void notifyItemInserted(int position) {
        notificationPublishSubject.onNext(of(ITEM_INSERTED, position, -1, null));
    }

    @Override
    public void notifyItemMoved(int fromPosition, int toPosition) {
        notificationPublishSubject.onNext(of(ITEM_MOVED, fromPosition, toPosition, null));
    }

    @Override
    public void notifyItemRangeInserted(int positionStart, int itemCount) {
        notificationPublishSubject.onNext(of(RANGE_INSERTED, positionStart, itemCount, null));
    }

    @Override
    public void notifyItemRemoved(int position) {
        notificationPublishSubject.onNext(of(ITEM_REMOVED, position, -1, null));
    }

    @Override
    public void notifyItemRangeRemoved(int positionStart, int itemCount) {
        notificationPublishSubject.onNext(of(ITEM_RANGE_REMOVED, positionStart, itemCount, null));
    }


}
