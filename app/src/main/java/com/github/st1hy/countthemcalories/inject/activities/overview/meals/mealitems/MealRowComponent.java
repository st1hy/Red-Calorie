package com.github.st1hy.countthemcalories.inject.activities.overview.meals.mealitems;

import com.github.st1hy.countthemcalories.activities.overview.meals.mealitems.MealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.meals.mealitems.SpaceMealItemHolder;

import dagger.Subcomponent;

@PerMealRow
@Subcomponent(modules = MealRowModule.class)
public interface MealRowComponent {

    MealItemHolder getMealHolder();

    SpaceMealItemHolder getSpaceHolder();
}
