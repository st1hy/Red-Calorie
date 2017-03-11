package com.github.st1hy.countthemcalories.activities.settings.fragment.inject;

import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

public interface SettingsFragmentComponentFactory {

    SettingsFragmentComponent newSettingsFragmentComponent(FragmentModule module);
}
