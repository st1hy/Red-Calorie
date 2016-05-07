package com.github.st1hy.countthemcalories.activities.settings.view;

import com.github.st1hy.countthemcalories.core.baseview.DialogView;

public interface SettingsView extends DialogView {

    void setLiquidUnit(String unitName);

    void setSolidUnit(String unitName);
}
