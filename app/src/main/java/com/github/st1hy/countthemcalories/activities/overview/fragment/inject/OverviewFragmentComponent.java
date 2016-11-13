package com.github.st1hy.countthemcalories.activities.overview.fragment.inject;

import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.activities.overview.inject.OverviewActivityComponent;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;

import dagger.Component;

@PerFragment
@Component(modules = {OverviewFragmentModule.class, PermissionModule.class},
        dependencies = OverviewActivityComponent.class)
public interface OverviewFragmentComponent {

    void inject(OverviewFragment fragment);
}
