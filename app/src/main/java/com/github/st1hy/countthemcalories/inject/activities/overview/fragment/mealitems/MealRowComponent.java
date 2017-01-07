package com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems;

import com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.MealItemHolder;

import dagger.Subcomponent;

@PerMealRow
@Subcomponent(modules = MealRowModule.class)
public interface MealRowComponent {

    MealItemHolder getHolder();

}
