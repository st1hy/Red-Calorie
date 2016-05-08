package com.github.st1hy.countthemcalories.activities.settings.presenter;

import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;

public interface SettingsPresenter extends DrawerPresenter {

    void onLiquidUnitSettingsClicked();

    void onSolidUnitSettingsClicked();

    void showCurrentUnits();

}
