package com.github.st1hy.countthemcalories.inject.activities.ingredients.viewholder;

import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.IngredientSpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.ingredientitems.PerIngredientRow;

import dagger.Subcomponent;

@PerIngredientRow
@Subcomponent(modules = IngredientViewHolderModule.class)
public interface IngredientViewHolderComponent {

    IngredientViewHolder newIngredientViewHolder();

    IngredientSpaceViewHolder newSpaceViewHolder();
}
