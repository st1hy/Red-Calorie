package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;

import java.math.BigDecimal;

import javax.inject.Inject;

public class UnitNamesModel {
    final SettingsModel settingsModel;
    final Resources resources;

    @Inject
    public UnitNamesModel(@NonNull SettingsModel settingsModel, @NonNull Resources resources) {
        this.settingsModel = settingsModel;
        this.resources = resources;
    }

    @NonNull
    public String getReadableEnergyDensity(@NonNull EnergyDensity energyDensity) {
        AmountUnitType amountUnitType = energyDensity.getAmountUnitType();
        EnergyDensityUnit preferredUnitOfType = settingsModel.getPreferredFrom(amountUnitType);
        EnergyDensity convertedToPreferred = energyDensity.convertTo(preferredUnitOfType);
        return settingsModel.getUnitPlural(convertedToPreferred);
    }

    @NonNull
    public String getReadableAmount(@NonNull BigDecimal amount, @NonNull EnergyDensityUnit unit) {
        return resources.getString(getUnitFormat(unit), amount.toPlainString());
    }

    @StringRes
    private int getUnitFormat(@NonNull EnergyDensityUnit unit) {
        switch (unit.getAmountUnitType()) {
            case VOLUME:
                return R.string.format_milliliter;
            case MASS:
                return R.string.format_gram;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
