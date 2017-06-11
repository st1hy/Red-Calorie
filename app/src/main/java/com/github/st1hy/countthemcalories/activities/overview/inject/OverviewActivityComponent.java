package com.github.st1hy.countthemcalories.activities.overview.inject;

import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.GraphComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.mealpager.inject.MealsPagerComponent;
import com.github.st1hy.countthemcalories.activities.overview.meals.inject.OverviewFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        OverviewActivityModule.class,
})
public interface OverviewActivityComponent extends OverviewFragmentComponentFactory,
        GraphComponentFactory,
        MealsPagerComponent {

    void inject(OverviewActivity activity);

}
