package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import java.math.BigDecimal;

import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.getDefaultAmountUnit;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.getDefaultEnergyUnit;

/**
 * Immutable energy density
 */
public class EnergyDensity {
    private final EnergyUnit energyUnit;
    private final AmountUnit unit;
    private final BigDecimal value;

    public EnergyDensity(@NonNull EnergyUnit energyUnit, @NonNull AmountUnit unit, @NonNull BigDecimal value) {
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

    @NonNull
    public BigDecimal getValue() {
        return value;
    }

    @NonNull
    public AmountUnitType getAmountUnitType() {
        return unit.getType();
    }

    @Override
    public String toString() {
        return value.toPlainString() + " " + energyUnit + " / " + unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyDensity that = (EnergyDensity) o;

        return energyUnit == that.energyUnit && unit.equals(that.unit) && value.equals(that.value);

    }

    @Override
    public int hashCode() {
        int result = energyUnit.hashCode();
        result = 31 * result + unit.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @NonNull
    public EnergyDensity convertTo(@NonNull EnergyUnit energyUnit, @NonNull AmountUnit unit) {
        if (unit.getType() != getAmountUnitType())
            throw new IllegalArgumentException("Cannot convert energy density using this method; different amount unit type");

        final BigDecimal sourceEnergyBase = getEnergyUnit().getBase();
        final BigDecimal sourceAmountBase = getAmountUnit().getBase();
        final BigDecimal targetEnergyBase = energyUnit.getBase();
        final BigDecimal targetAmountBase = unit.getBase();

        BigDecimal convertedValue = value.multiply(sourceEnergyBase)
                .multiply(targetAmountBase)
                .divide(targetEnergyBase.multiply(sourceAmountBase), EnergyDensityUtils.DEFAULT_PRECISION)
                .stripTrailingZeros();
        return new EnergyDensity(energyUnit, unit, convertedValue);
    }

    @NonNull
    public static EnergyDensity from(@NonNull IngredientTemplate template) {
        return fromDatabaseValue(template.getAmountType(), template.getEnergyDensityAmount());
    }

    @NonNull
    public static EnergyDensity fromDatabaseValue(@NonNull AmountUnitType amountType, @NonNull BigDecimal amount) {
        return new EnergyDensity(getDefaultEnergyUnit(), getDefaultAmountUnit(amountType), amount);
    }

    @NonNull
    public EnergyDensity convertToDatabaseFormat() {
        return convertTo(getDefaultEnergyUnit(), getDefaultAmountUnit(getAmountUnitType()));
    }

}
