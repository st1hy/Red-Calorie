package com.github.st1hy.countthemcalories.activities.settings.view;

import com.github.st1hy.countthemcalories.core.baseview.DialogView;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;

public interface SettingsView extends DialogView, DrawerView {

    void setLiquidUnit(String unitName);

    void setSolidUnit(String unitName);
}
