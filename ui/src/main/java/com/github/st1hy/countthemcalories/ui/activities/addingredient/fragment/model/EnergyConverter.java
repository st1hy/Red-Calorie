package com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

import javax.inject.Inject;

@PerFragment
public class EnergyConverter {

    private final SettingsModel settingsModel;

    @Inject
    public EnergyConverter(SettingsModel settingsModel) {
        this.settingsModel = settingsModel;
    }

    @NonNull
    public AmountUnit getDefaultAmountUnitOf(AmountUnitType amountUnitType) {
        return settingsModel.getAmountUnitFrom(amountUnitType);
    }

    @NonNull
    public EnergyUnit getDefaultEnergyUnit() {
        return settingsModel.getEnergyUnit();
    }


    public double fromDatabaseToCurrent(@NonNull AmountUnitType type,
                                        double energyDensityAmount) {
        return EnergyDensity.fromDatabaseValue(type, energyDensityAmount)
                .convertTo(getDefaultEnergyUnit(), getDefaultAmountUnitOf(type))
                .getValue();
    }
}
