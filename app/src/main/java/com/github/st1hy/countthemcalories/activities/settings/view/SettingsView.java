package com.github.st1hy.countthemcalories.activities.settings.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.ui.view.BaseView;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

public interface SettingsView extends BaseView {

    void showUnitSettingsDialog(@NonNull AmountUnitType type, @NonNull String[] values);

    void setLiquidUnit(String unitName);

    void setSolidUnit(String unitName);
}
