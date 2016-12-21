package com.github.st1hy.countthemcalories.core.adapter;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;

public abstract class AbstractForwardingAdapter<R extends ListAdapter & Filterable> implements
        ListAdapter, Filterable {

    @NonNull
    protected abstract R getParent();

    @Override
    public boolean areAllItemsEnabled() {
        return getParent().areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return getParent().isEnabled(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        getParent().registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        getParent().unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return getParent().getCount();
    }

    @Override
    public Object getItem(int position) {
        return getParent().getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return getParent().getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return getParent().hasStableIds();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return this.getParent().getView(position, convertView, parent);
    }

    @Override
    public int getItemViewType(int position) {
        return getParent().getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return getParent().getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return getParent().isEmpty();
    }

    @Override
    public Filter getFilter() {
        return getParent().getFilter();
    }
}
