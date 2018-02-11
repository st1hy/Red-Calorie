package com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.adapter.inject.IngredientRootView;

public abstract class AbstractIngredientsViewHolder extends RecyclerView.ViewHolder {

    AbstractIngredientsViewHolder(@NonNull @IngredientRootView View itemView) {
        super(itemView);
    }

    public void onAttached() {
    }

    public void onDetached() {
    }
}
