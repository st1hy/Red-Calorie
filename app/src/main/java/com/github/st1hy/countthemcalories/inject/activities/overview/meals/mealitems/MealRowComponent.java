package com.github.st1hy.countthemcalories.inject.activities.overview.meals.mealitems;

import com.github.st1hy.countthemcalories.activities.overview.meals.mealitems.MealItemHolder;

import dagger.Subcomponent;

@PerMealRow
@Subcomponent(modules = MealRowModule.class)
public interface MealRowComponent {

    MealItemHolder getHolder();

}
