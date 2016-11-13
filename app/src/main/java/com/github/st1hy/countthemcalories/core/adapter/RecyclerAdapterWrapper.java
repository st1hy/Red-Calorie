package com.github.st1hy.countthemcalories.core.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface RecyclerAdapterWrapper<T extends RecyclerView.ViewHolder> {

    T onCreateViewHolder(ViewGroup parent, int viewType);

    void onBindViewHolder(T holder, int position);

    int getItemCount();

    void setNotifier(@NonNull RecyclerViewNotifier notifier);
}
