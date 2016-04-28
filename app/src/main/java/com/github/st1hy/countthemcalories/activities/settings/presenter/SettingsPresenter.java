package com.github.st1hy.countthemcalories.activities.settings.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

public interface SettingsPresenter {

    void onLiquidUnitSettingsClicked();

    void onSelectedUnitType(@NonNull AmountUnitType type, int which);

    void onSolidUnitSettingsClicked();

    void showCurrentUnits();

    void onStart();

    void onStop();
}
