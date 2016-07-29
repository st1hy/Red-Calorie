package com.github.st1hy.countthemcalories.activities.overview.fragment.inject;

import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;

import dagger.Component;

@PerFragment
@Component(modules = {OverviewFragmentModule.class, PermissionModule.class},
        dependencies = ApplicationComponent.class)
public interface OverviewFragmentComponent {

    void inject(OverviewFragment fragment);
}
