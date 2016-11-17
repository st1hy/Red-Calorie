package com.github.st1hy.countthemcalories.inject.activities.overview;

import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.OverviewFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.core.DrawerModule;
import com.github.st1hy.countthemcalories.inject.core.UndoModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        OverviewActivityModule.class,
        DrawerModule.class,
        UndoModule.class
})
public interface OverviewActivityComponent extends OverviewFragmentComponentFactory {

    void inject(OverviewActivity activity);

}
