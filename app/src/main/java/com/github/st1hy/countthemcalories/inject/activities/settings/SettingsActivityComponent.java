package com.github.st1hy.countthemcalories.inject.activities.settings;

import com.github.st1hy.countthemcalories.activities.settings.SettingsActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.settings.fragment.SettingsFragmentComponentFactory;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = SettingsActivityModule.class)
public interface SettingsActivityComponent extends SettingsFragmentComponentFactory {

    void inject(SettingsActivity activity);
}
