package com.github.st1hy.countthemcalories.core.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface RecyclerAdapterWrapper<T extends RecyclerView.ViewHolder> {

    T onCreateViewHolder(ViewGroup parent, int viewType);

    void onBindViewHolder(T holder, int position);

    int getItemCount();
}
