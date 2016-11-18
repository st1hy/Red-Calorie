package com.github.st1hy.countthemcalories.core.adapter.delegate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.core.BasicLifecycle;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class RecyclerViewAdapterDelegate<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T>
        implements RecyclerViewNotifier, BasicLifecycle, Action1<RecyclerViewNotification> {

    @NonNull
    private final RecyclerAdapterWrapper<T> delegate;
    @NonNull
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public RecyclerViewAdapterDelegate(@NonNull RecyclerAdapterWrapper<T> delegate) {
        this.delegate = delegate;
    }

    public static <T extends RecyclerView.ViewHolder> RecyclerViewAdapterDelegate<T> newAdapter(RecyclerAdapterWrapper<T> delegate) {
        return new RecyclerViewAdapterDelegate<>(delegate);
    }

    @Override
    public void onStart() {
        subscriptions.add(delegate.asObservable().subscribe(this));
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return delegate.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        delegate.onBindViewHolder(holder, position);
    }

    @Override
    public void onViewAttachedToWindow(T holder) {
        super.onViewAttachedToWindow(holder);
        delegate.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(T holder) {
        super.onViewDetachedFromWindow(holder);
        delegate.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return delegate.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return delegate.getItemViewType(position);
    }

    @Override
    public void call(RecyclerViewNotification recyclerViewNotification) {
        recyclerViewNotification.call(this);
    }
}
