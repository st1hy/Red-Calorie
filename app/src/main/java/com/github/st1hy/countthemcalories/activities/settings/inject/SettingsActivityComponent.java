package com.github.st1hy.countthemcalories.activities.settings.inject;

import com.github.st1hy.countthemcalories.activities.settings.fragment.inject.SettingsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = SettingsActivityModule.class)
public interface SettingsActivityComponent extends SettingsFragmentComponentFactory {

    void inject(SettingsActivity activity);
}
