package com.github.st1hy.countthemcalories.activities.settings.inject;


import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = SettingsModule.class, dependencies = ApplicationComponent.class)
public interface SettingsComponent {

    void inject(SettingsActivity activity);

}
