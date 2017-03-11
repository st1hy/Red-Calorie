package com.github.st1hy.countthemcalories.activities.settings.inject;

import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

public interface SettingsActivityComponentFactory {

    SettingsActivityComponent newSettingsActivityComponent(ActivityModule module);
}
