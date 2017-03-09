package com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.inject.IngredientRootView;

import javax.inject.Inject;

public class IngredientSpaceViewHolder extends AbstractIngredientsViewHolder {

    @Inject
    public IngredientSpaceViewHolder(@NonNull @IngredientRootView View itemView) {
        super(itemView);
    }
}
