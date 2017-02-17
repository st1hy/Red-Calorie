package com.github.st1hy.countthemcalories.inject.activities.overview;

import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.GraphComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.mealpager.MealsPagerComponent;
import com.github.st1hy.countthemcalories.activities.overview.mealpager.MealsPagerModule;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.overview.meals.OverviewFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.core.ActivityLauncherModule;
import com.github.st1hy.countthemcalories.inject.core.DrawerModule;
import com.github.st1hy.countthemcalories.inject.core.UndoModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        OverviewActivityModule.class,
        DrawerModule.class,
        UndoModule.class,
        ActivityLauncherModule.class,
        MealsPagerModule.class
})
public interface OverviewActivityComponent extends OverviewFragmentComponentFactory,
        GraphComponentFactory,
        MealsPagerComponent {

    void inject(OverviewActivity activity);

}
