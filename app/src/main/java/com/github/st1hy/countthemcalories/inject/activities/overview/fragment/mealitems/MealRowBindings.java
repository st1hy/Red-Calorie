package com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems;

import com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.AbstractMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.EmptyMealItemHolder;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MealRowBindings {

    @Binds
    @Named("emptySpace")
    public abstract AbstractMealItemHolder emptySpace(EmptyMealItemHolder emptyMealItemHolder);
}
