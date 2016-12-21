package com.github.st1hy.countthemcalories.inject.activities.settings.fragment;


import com.github.st1hy.countthemcalories.activities.settings.fragment.SettingsFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.core.DialogModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {SettingsFragmentModule.class, DialogModule.class})
public interface SettingsFragmentComponent {

    void inject(SettingsFragment fragment);

}
