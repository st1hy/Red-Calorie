package com.github.st1hy.countthemcalories.activities.overview.meals.inject;

import com.github.st1hy.countthemcalories.activities.overview.meals.MealsFragment;
import com.github.st1hy.countthemcalories.activities.overview.meals.adapter.inject.MealRowComponentFactory;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        OverviewFragmentModule.class,
})
public interface OverviewFragmentComponent extends MealRowComponentFactory {

    void inject(MealsFragment fragment);
}
