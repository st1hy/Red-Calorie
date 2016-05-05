package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;

import java.math.BigDecimal;

/**
 * Immutable energy density
 */
public class EnergyDensity {
    private final EnergyDensityUnit unit;
    private final BigDecimal value;

    public EnergyDensity(@NonNull EnergyDensityUnit unit, @NonNull BigDecimal value) {
        this.unit = unit;
        this.value = value;
    }

    public EnergyDensity(@NonNull EnergyDensity gravimetricEnergyDensity) {
        this.unit = gravimetricEnergyDensity.getUnit();
        this.value = gravimetricEnergyDensity.getValue();
    }

    @NonNull
    public EnergyDensityUnit getUnit() {
        return unit;
    }

    @NonNull
    public BigDecimal getValue() {
        return value;
    }

    @PluralsRes
    public int getPluralResId() {
        return unit.getPluralResId();
    }

    @NonNull
    public AmountUnitType getAmountUnitType() {
        return unit.getAmountUnitType();
    }

    @Override
    public String toString() {
        return value.toString() + " " + unit.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnergyDensity that = (EnergyDensity) o;

        return unit.equals(that.unit) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = unit.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    /**
     * Converts energy density to other unit.
     *
     * @param unit targeted unit
     * @return new energy density
     */
    @NonNull
    public EnergyDensity convertTo(@NonNull EnergyDensityUnit unit) {
        if (unit.getAmountUnitType() != getAmountUnitType())
            throw new IllegalArgumentException("Cannot convert energy density using this method; unknown mass density");
        return new EnergyDensity(unit, this.unit.convertValue(value, unit));
    }
}
