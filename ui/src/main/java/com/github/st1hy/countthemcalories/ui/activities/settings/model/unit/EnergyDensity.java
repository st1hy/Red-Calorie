package com.github.st1hy.countthemcalories.ui.activities.settings.model.unit;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplate;

/**
 * Immutable energy density
 */
public class EnergyDensity {
    private final EnergyUnit energyUnit;
    private final AmountUnit unit;
    private final double value;

    public EnergyDensity(@NonNull EnergyUnit energyUnit,
                         @NonNull AmountUnit unit,
                         double value) {
        this.energyUnit = energyUnit;
        this.unit = unit;
        this.value = value;
    }

    public EnergyDensity(@NonNull EnergyDensity energyDensity) {
        this.energyUnit = energyDensity.energyUnit;
        this.unit = energyDensity.unit;
        this.value = energyDensity.value;
    }

    @NonNull
    public EnergyUnit getEnergyUnit() {
        return energyUnit;
    }

    @NonNull
    public AmountUnit getAmountUnit() {
        return unit;
    }

    public double getValue() {
        return value;
    }

    @NonNull
    public AmountUnitType getAmountUnitType() {
        return unit.getType();
    }

    @Override
    public String toString() {
        return Double.toString(value) + " " + energyUnit + " / " + unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyDensity that = (EnergyDensity) o;

        return Double.compare(that.value, value) == 0 && energyUnit == that.energyUnit
                && unit.equals(that.unit);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = energyUnit.hashCode();
        result = 31 * result + unit.hashCode();
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @NonNull
    public EnergyDensity convertTo(@NonNull EnergyUnit energyUnit, @NonNull AmountUnit unit) {
        if (unit.getType() != getAmountUnitType())
            throw new IllegalArgumentException("Cannot convert energy density using this method; different amount unit type");

        final double sourceEnergyBase = getEnergyUnit().getBase();
        final double sourceAmountBase = getAmountUnit().getBase();
        final double targetEnergyBase = energyUnit.getBase();
        final double targetAmountBase = unit.getBase();

        double convertedValue = value * sourceEnergyBase * targetAmountBase
                / (targetEnergyBase * sourceAmountBase);
        return new EnergyDensity(energyUnit, unit, convertedValue);
    }

    @NonNull
    public static EnergyDensity from(@NonNull IngredientTemplate template) {
        return fromDatabaseValue(template.getAmountType(), template.getEnergyDensityAmount());
    }

    @NonNull
    public static EnergyDensity fromDatabaseValue(@NonNull AmountUnitType amountType,
                                                  double amount) {
        return new EnergyDensity(EnergyDensityUtils.getDefaultEnergyUnit(), EnergyDensityUtils.getDefaultAmountUnit(amountType), amount);
    }

    @NonNull
    public EnergyDensity convertToDatabaseFormat() {
        return convertTo(EnergyDensityUtils.getDefaultEnergyUnit(), EnergyDensityUtils.getDefaultAmountUnit(getAmountUnitType()));
    }

}
