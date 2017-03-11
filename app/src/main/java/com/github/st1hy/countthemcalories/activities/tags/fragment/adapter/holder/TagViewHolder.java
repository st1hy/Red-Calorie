package com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class TagViewHolder extends RecyclerView.ViewHolder {


    TagViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    public abstract void onAttached();

    public abstract void onDetached();
}
