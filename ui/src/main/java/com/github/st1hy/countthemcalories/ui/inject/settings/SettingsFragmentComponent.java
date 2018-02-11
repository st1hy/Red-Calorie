package com.github.st1hy.countthemcalories.ui.inject.settings;


import com.github.st1hy.countthemcalories.ui.activities.settings.view.SettingsFragment;
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        SettingsFragmentModule.class,
        DialogModule.class
})
public interface SettingsFragmentComponent {

    void inject(SettingsFragment fragment);

}
