package com.github.st1hy.countthemcalories.activities.overview.fragment.inject;

import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import dagger.Component;

@PerFragment
@Component(modules = OverviewFragmentModule.class, dependencies = ApplicationComponent.class)
public interface OverviewFragmentComponent {

    void inject(OverviewFragment fragment);
}
