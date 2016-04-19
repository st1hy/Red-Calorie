package com.github.st1hy.countthemcalories.activities.overview.inject;

import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;

import dagger.Component;

@PerActivity
@Component(modules = OverviewActivityModule.class, dependencies = ApplicationComponent.class)
public interface OverviewActivityComponent {

    void inject(OverviewActivity activity);
}
