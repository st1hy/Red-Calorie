package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.rx.ObservableValue;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class AddIngredientModel {
    private static String STATE_UNIT = "unit";

    private final SettingsModel settingsModel;

    private final ObservableValue<EnergyDensityUnit> unit;

    @Inject
    public AddIngredientModel(@NonNull SettingsModel settingsModel, @Nullable Bundle savedState) {
        this.settingsModel = settingsModel;
        this.unit = new ObservableValue<>(getUnit(savedState));
    }

    @NonNull
    private EnergyDensityUnit getUnit(@Nullable Bundle savedState) {
        EnergyDensityUnit unit = null;
        if (savedState != null) {
            String unitString = savedState.getString(STATE_UNIT);
            if (unitString != null)
                unit = EnergyDensityUtils.fromString(unitString);
        }
        if (unit == null)
            unit = settingsModel.getPreferredGravimetricUnit();
        return unit;
    }

    public void onSaveState(@NonNull Bundle outState) {
        outState.putInt(STATE_UNIT, unit.getValue().getId());
    }

    public void setUnit(@NonNull EnergyDensityUnit unit) {
        this.unit.setValue(unit);
    }

    @NonNull
    public Observable<EnergyDensityUnit> getUnitSubject() {
        return unit.asObservable();
    }

    @NonNull
    public EnergyDensityUnit[] getUnitSelection() {
        return new EnergyDensityUnit[]{
                settingsModel.getPreferredGravimetricUnit(),
                settingsModel.getPreferredVolumetricUnit()
        };
    }

    @NonNull
    public String[] getUnitSelectionString() {
        EnergyDensityUnit[] unitSelection = getUnitSelection();
        String[] options = new String[unitSelection.length];
        for (int i = 0; i < unitSelection.length; i++) {
            options[i] = settingsModel.getUnitName(unitSelection[i]);
        }
        return options;
    }

    @NonNull
    public Func1<EnergyDensityUnit, String> unitAsString() {
        return new Func1<EnergyDensityUnit, String>() {
            @Override
            public String call(EnergyDensityUnit energyDensityUnit) {
                return settingsModel.getUnitName(energyDensityUnit);
            }
        };
    }
}
