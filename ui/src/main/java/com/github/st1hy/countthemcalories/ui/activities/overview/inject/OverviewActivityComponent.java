package com.github.st1hy.countthemcalories.ui.activities.overview.inject;

import com.github.st1hy.countthemcalories.ui.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject.GraphComponentFactory;
import com.github.st1hy.countthemcalories.ui.activities.overview.mealpager.inject.MealsPagerComponent;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.inject.OverviewFragmentComponentFactory;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;

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
