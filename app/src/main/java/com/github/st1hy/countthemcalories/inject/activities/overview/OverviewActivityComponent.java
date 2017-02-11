package com.github.st1hy.countthemcalories.inject.activities.overview;

import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.GraphComponentFactory;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.OverviewFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.core.ActivityLauncherModule;
import com.github.st1hy.countthemcalories.inject.core.DrawerModule;
import com.github.st1hy.countthemcalories.inject.core.UndoModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        OverviewActivityModule.class,
        DrawerModule.class,
        UndoModule.class,
        ActivityLauncherModule.class
})
public interface OverviewActivityComponent extends OverviewFragmentComponentFactory, GraphComponentFactory {

    void inject(OverviewActivity activity);

}
