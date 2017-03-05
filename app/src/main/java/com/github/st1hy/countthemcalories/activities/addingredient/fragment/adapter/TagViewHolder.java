package com.github.st1hy.countthemcalories.activities.addingredient.fragment.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

abstract class TagViewHolder extends RecyclerView.ViewHolder {

    TagViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
