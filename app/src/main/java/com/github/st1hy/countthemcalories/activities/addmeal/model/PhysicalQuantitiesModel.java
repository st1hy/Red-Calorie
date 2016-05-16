package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.Unit;

import java.math.BigDecimal;

import javax.inject.Inject;

public class PhysicalQuantitiesModel {
    final SettingsModel settingsModel;
    final Resources resources;

    @Inject
    public PhysicalQuantitiesModel(@NonNull SettingsModel settingsModel, @NonNull Resources resources) {
        this.settingsModel = settingsModel;
        this.resources = resources;
    }

    @NonNull
    public EnergyDensity convertToPreferred(@NonNull EnergyDensity energyDensity) {
        AmountUnit preferredAmountUnit = settingsModel.getAmountUnitFrom(energyDensity.getAmountUnitType());
        EnergyUnit preferredEnergyUnit = settingsModel.getEnergyUnit();
        return energyDensity.convertTo(preferredEnergyUnit, preferredAmountUnit);
    }

    @NonNull
    public String format(@NonNull EnergyDensity energyDensity) {
        String energyUnit = resources.getString(energyDensity.getEnergyUnit().getNameRes());
        String amountUnit = resources.getString(energyDensity.getAmountUnit().getNameRes());
        String value = energyDensity.getValue()
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
        return resources.getString(R.string.format_value_fraction, value, energyUnit, amountUnit);
    }

    @NonNull
    public String convertAndFormat(@NonNull EnergyDensity energyDensity) {
        return format(convertToPreferred(energyDensity));
    }

//    @NonNull
//    public AmountUnit convertToPreferred(@NonNull AmountUnit amountUnit) {
//        return settingsModel.getAmountUnitFrom(amountUnit.getType());
//    }

    @NonNull
    public String format(@NonNull BigDecimal amount, @NonNull AmountUnit unit) {
        return resources.getString(R.string.format_value_simple, amount.toPlainString(), getUnitName(unit));
    }

    @NonNull
    public String getUnitName(@NonNull Unit unit) {
        return settingsModel.getUnitName(unit);
    }

//    @NonNull
//    public String convertAndFormat(@NonNull BigDecimal amount, @NonNull AmountUnit unit) {
//        return format(amount, convertToPreferred(unit));
//    }

    /**
     * Calculates and formats energy from amount and energy density
     *
     * @param amount        how much substance it is
     * @param amountUnit    is what unit amount is
     * @param energyDensity what is its energy density
     * @return formatted value of energy
     */
    @NonNull
    public String formatEnergyCount(@NonNull BigDecimal amount, @NonNull AmountUnit amountUnit,
                                    @NonNull EnergyDensity energyDensity) {
        String amountString = energyDensity.getValue()
                .multiply(amount)
                .multiply(amountUnit.getBase())
                .divide(energyDensity.getAmountUnit().getBase(), EnergyDensityUtils.DEFAULT_PRECISION)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
        String energyUnitName = getUnitName(energyDensity.getEnergyUnit());
        return resources.getString(R.string.format_value_simple, amountString, energyUnitName);
    }

    @NonNull
    public String getUnitNameFrom(@NonNull AmountUnitType amountUnitType) {
        return getUnitName(settingsModel.getAmountUnitFrom(amountUnitType));
    }

    @NonNull
    public BigDecimal convertAmountFromDatabase(@NonNull BigDecimal databaseAmount,
                                                @NonNull AmountUnit targetUnit) {
        AmountUnit databaseUnit = EnergyDensityUtils.getDefaultAmountUnit(targetUnit.getType());
        return convertAmount(databaseAmount, databaseUnit, targetUnit);
    }

    @NonNull
    public BigDecimal convertAmountToDatabase(@NonNull BigDecimal sourceAmount,
                                              @NonNull AmountUnit sourceUnit) {
        AmountUnit databaseUnit = EnergyDensityUtils.getDefaultAmountUnit(sourceUnit.getType());
        return convertAmount(sourceAmount, sourceUnit, databaseUnit);
    }

    @NonNull
    public BigDecimal convertAmount(@NonNull BigDecimal amount,
                                    @NonNull AmountUnit from,
                                    @NonNull AmountUnit to) {
        return amount.multiply(to.getBase())
                .divide(from.getBase(), EnergyDensityUtils.DEFAULT_PRECISION)
                .stripTrailingZeros();
    }
}
