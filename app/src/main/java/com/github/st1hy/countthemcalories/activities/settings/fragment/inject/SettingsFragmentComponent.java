package com.github.st1hy.countthemcalories.activities.settings.fragment.inject;


import com.github.st1hy.countthemcalories.activities.settings.fragment.SettingsFragment;
import com.github.st1hy.countthemcalories.core.dialog.DialogModule;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

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
