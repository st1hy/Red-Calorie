package com.github.st1hy.countthemcalories.activities.settings.inject;

import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = SettingsActivityModule.class)
public interface SettingsActivityComponent {

    void inject(SettingsActivity activity);
}
