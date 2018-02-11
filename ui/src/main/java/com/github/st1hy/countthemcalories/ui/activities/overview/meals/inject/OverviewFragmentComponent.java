package com.github.st1hy.countthemcalories.ui.activities.overview.meals.inject;

import com.github.st1hy.countthemcalories.ui.activities.overview.meals.MealsFragment;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.inject.MealRowComponentFactory;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        OverviewFragmentModule.class,
})
public interface OverviewFragmentComponent extends MealRowComponentFactory {

    void inject(MealsFragment fragment);
}
