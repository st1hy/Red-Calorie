package com.github.st1hy.countthemcalories.activities.settings.fragment.inject;


import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SettingsFragment;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import dagger.Component;

@PerFragment
@Component(modules = SettingsFragmentModule.class, dependencies = ApplicationComponent.class)
public interface SettingsFragmentComponent {

    void inject(SettingsFragment fragment);

}
