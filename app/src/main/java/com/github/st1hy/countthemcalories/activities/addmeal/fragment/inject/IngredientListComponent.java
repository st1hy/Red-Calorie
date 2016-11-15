package com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.IngredientItemViewHolder;

import dagger.Subcomponent;

@PerIngredientRow
@Subcomponent(modules = IngredientListModule.class)
public interface IngredientListComponent {

    IngredientItemViewHolder getHolder();

}
