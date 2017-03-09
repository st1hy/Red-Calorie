package com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.inject;

import com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.viewholder.IngredientSpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.inject.PerIngredientRow;

import dagger.Subcomponent;

@PerIngredientRow
@Subcomponent(modules = IngredientViewHolderModule.class)
public interface IngredientViewHolderComponent {

    IngredientViewHolder newIngredientViewHolder();

    IngredientSpaceViewHolder newSpaceViewHolder();
}
