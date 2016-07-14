package com.github.st1hy.countthemcalories.activities.addingredient.fragment.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public abstract class TagViewHolder extends RecyclerView.ViewHolder {

    public TagViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
