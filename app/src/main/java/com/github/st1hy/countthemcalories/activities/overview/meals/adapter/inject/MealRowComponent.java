package com.github.st1hy.countthemcalories.activities.overview.meals.adapter.inject;

import com.github.st1hy.countthemcalories.activities.overview.meals.adapter.holder.MealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.meals.adapter.holder.SpaceMealItemHolder;

import dagger.Subcomponent;

@PerMealRow
@Subcomponent(modules = MealRowModule.class)
public interface MealRowComponent {

    MealItemHolder getMealHolder();

    SpaceMealItemHolder getSpaceHolder();
}
