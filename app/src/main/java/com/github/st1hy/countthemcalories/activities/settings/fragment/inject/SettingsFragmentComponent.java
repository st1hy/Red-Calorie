package com.github.st1hy.countthemcalories.activities.settings.fragment.inject;


import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SettingsFragment;
import com.github.st1hy.countthemcalories.core.dialog.DialogModule;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {SettingsFragmentModule.class, DialogModule.class})
public interface SettingsFragmentComponent {

    void inject(SettingsFragment fragment);

}
