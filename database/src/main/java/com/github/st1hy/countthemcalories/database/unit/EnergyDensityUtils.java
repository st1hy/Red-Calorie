package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.math.MathContext;

import timber.log.Timber;

public class EnergyDensityUtils {
    public static final MathContext DEFAULT_PRECISION = MathContext.DECIMAL64;

    /**
     * @return returns Energy density of provided unit and string representation of big decimal value
     * or 0 if string is not in correct format
     */
    @NonNull
    public static EnergyDensity getOrZero(@NonNull EnergyUnit energyUnit, @NonNull AmountUnit unit,
                                          @NonNull String energyValue) {
        return new EnergyDensity(energyUnit, unit, getOrZero(energyValue));
    }

    /**
     * @return returns big decimal value of string or 0 if string is not in correct format
     */
    public static BigDecimal getOrZero(@NonNull String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            Timber.w("Value: '%s' is not valid BigInteger", value);
            return BigDecimal.ZERO;
        }
    }

    @NonNull
    public static EnergyUnit getDefaultEnergyUnit() {
        return EnergyUnit.KJ;
    }

    @NonNull
    public static AmountUnit getDefaultAmountUnit(@NonNull AmountUnitType amountType) {
        switch (amountType) {
            case VOLUME:
                return VolumeUnit.ML;
            case MASS:
                return MassUnit.G;
            case UNKNOWN:
            default:
                throw new UnsupportedOperationException();
        }
    }
}
