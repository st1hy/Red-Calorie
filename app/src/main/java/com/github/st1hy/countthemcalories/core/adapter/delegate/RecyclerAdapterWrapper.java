package com.github.st1hy.countthemcalories.core.adapter.delegate;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public abstract class RecyclerAdapterWrapper<T extends RecyclerView.ViewHolder> extends RecyclerViewNotifierObservable {

    public abstract T onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(T holder, int position);

    public abstract int getItemCount();

    public void onViewAttachedToWindow(T holder) {
    }

    public void onViewDetachedFromWindow(T holder){
    }

    public int getItemViewType(int position) {
        return 0;
    }
}
