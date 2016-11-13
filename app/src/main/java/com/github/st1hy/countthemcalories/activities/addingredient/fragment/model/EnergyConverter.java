package com.github.st1hy.countthemcalories.activities.addingredient.fragment.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;

import java.math.BigDecimal;

import javax.inject.Inject;

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


    @NonNull
    public BigDecimal fromDatabaseToCurrent(@NonNull AmountUnitType type,
                                            @NonNull BigDecimal energyDensityAmount) {
        return EnergyDensity.fromDatabaseValue(type, energyDensityAmount)
                .convertTo(getDefaultEnergyUnit(), getDefaultAmountUnitOf(type))
                .getValue()
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros();
    }
}
