package com.github.st1hy.countthemcalories.core.adapter;

import android.support.annotation.NonNull;
import android.widget.Filterable;
import android.widget.ListAdapter;

public class ForwardingAdapter<R extends ListAdapter & Filterable> extends AbstractForwardingAdapter<R> {

    final R parent;

    public ForwardingAdapter(@NonNull R parent) {
        this.parent = parent;
    }

    @NonNull
    @Override
    protected R getParent() {
        return parent;
    }
}
