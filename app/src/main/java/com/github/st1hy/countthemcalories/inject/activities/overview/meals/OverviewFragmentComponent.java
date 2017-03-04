package com.github.st1hy.countthemcalories.inject.activities.overview.meals;

import com.github.st1hy.countthemcalories.activities.overview.meals.MealsFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.overview.meals.mealitems.MealRowComponentFactory;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        OverviewFragmentModule.class,
        PermissionModule.class,
})
public interface OverviewFragmentComponent extends MealRowComponentFactory {

    void inject(MealsFragment fragment);
}
