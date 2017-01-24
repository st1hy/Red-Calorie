package com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder;

import android.support.annotation.NonNull;
import android.view.View;

import javax.inject.Inject;
import javax.inject.Named;

public class IngredientSpaceViewHolder extends AbstractIngredientsViewHolder {

    @Inject
    public IngredientSpaceViewHolder(@NonNull @Named("ingredientRootView") View itemView) {
        super(itemView);
    }
}
