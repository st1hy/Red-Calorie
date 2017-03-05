package com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.inject;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.IngredientItemViewHolder;

import dagger.Subcomponent;

@PerIngredientRow
@Subcomponent(modules = IngredientListModule.class)
public interface IngredientListComponent {

    IngredientItemViewHolder getHolder();

}
