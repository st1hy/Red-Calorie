package com.github.st1hy.countthemcalories.activities.settings.inject;

import com.github.st1hy.countthemcalories.activities.settings.SettingsActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.activities.settings.fragment.inject.SettingsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        SettingsActivityModule.class
})
public interface SettingsActivityComponent extends SettingsFragmentComponentFactory {

    void inject(SettingsActivity activity);
}
