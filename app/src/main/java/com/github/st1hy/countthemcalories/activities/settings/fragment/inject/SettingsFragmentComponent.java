package com.github.st1hy.countthemcalories.activities.settings.fragment.inject;


import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SettingsFragment;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = SettingsFragmentModule.class)
public interface SettingsFragmentComponent {

    void inject(SettingsFragment fragment);

}
