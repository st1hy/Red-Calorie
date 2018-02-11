package com.github.st1hy.countthemcalories.ui.inject.settings;

import com.github.st1hy.countthemcalories.ui.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        SettingsActivityModule.class
})
public interface SettingsActivityComponent extends SettingsFragmentComponentFactory {

    void inject(SettingsActivity activity);
}
