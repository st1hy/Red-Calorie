package com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.ingredientitems;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.ingredientitems.IngredientItemViewHolder;

import dagger.Subcomponent;

@PerIngredientRow
@Subcomponent(modules = IngredientListModule.class)
public interface IngredientListComponent {

    IngredientItemViewHolder getHolder();

}
