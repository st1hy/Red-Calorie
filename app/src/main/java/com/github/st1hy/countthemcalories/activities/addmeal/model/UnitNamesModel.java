package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;

import java.math.BigDecimal;
import java.math.MathContext;

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
        return settingsModel.getUnitName(convertedToPreferred);
    }

    @NonNull
    public String getReadableAmount(@NonNull BigDecimal amount, @NonNull EnergyDensityUnit unit) {
        return resources.getString(getUnitFormat(unit), amount.toPlainString());
    }

    @StringRes
    public int getUnitFormat(@NonNull EnergyDensityUnit unit) {
        switch (unit.getAmountUnitType()) {
            case VOLUME:
                return R.string.format_milliliter;
            case MASS:
                return R.string.format_gram;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @NonNull
    public String getCalorieCount(@NonNull BigDecimal amount, @NonNull EnergyDensity energyDensity) {
        AmountUnitType amountUnitType = energyDensity.getAmountUnitType();
        EnergyDensityUnit preferredUnitOfType = settingsModel.getPreferredFrom(amountUnitType);
        EnergyDensity convertedToPreferred = energyDensity.convertTo(preferredUnitOfType);
        EnergyUnit energyUnit = preferredUnitOfType.getEnergyUnit();
        return resources.getString(getUnitFormat(energyUnit), convertedToPreferred.getValue()
                .multiply(amount)
                .divide(preferredUnitOfType.getAmountBase(), MathContext.DECIMAL64)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros()
                .toPlainString());
    }


    @StringRes
    public int getUnitFormat(@NonNull EnergyUnit unit) {
        switch (unit) {
            case KCAL:
                return R.string.format_kcal;
            case KJ:
                return R.string.format_kj;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @StringRes
    public int getUnitName(@NonNull EnergyDensityUnit unit) {
        switch (unit.getAmountUnitType()) {
            case VOLUME:
                return R.string.unit_milliliter;
            case MASS:
                return R.string.unit_gram;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
