package com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import javax.inject.Named;

public abstract class AbstractIngredientsViewHolder extends RecyclerView.ViewHolder {

    AbstractIngredientsViewHolder(@NonNull @Named("ingredientRootView") View itemView) {
        super(itemView);
    }

    public void onAttached() {
    }

    public void onDetached() {
    }
}
