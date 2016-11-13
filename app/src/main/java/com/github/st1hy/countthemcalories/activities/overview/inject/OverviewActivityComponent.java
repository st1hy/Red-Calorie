package com.github.st1hy.countthemcalories.activities.overview.inject;

import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.drawer.DrawerModule;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = {OverviewActivityModule.class, DrawerModule.class},
        dependencies = ApplicationComponent.class)
public interface OverviewActivityComponent {

    void inject(OverviewActivity activity);

    OverviewScreen overviewScreen();
}
