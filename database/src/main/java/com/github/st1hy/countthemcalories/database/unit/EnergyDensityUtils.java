package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.BuildConfig;

import java.math.BigDecimal;
import java.math.MathContext;

import timber.log.Timber;

public class EnergyDensityUtils {
    static final BigDecimal ONE = BigDecimal.ONE;
    static final BigDecimal HUNDRED = BigDecimal.valueOf(1, -2);

    public static Enum<? extends EnergyDensityUnit>[] getUnits(@NonNull AmountUnitType unitType) {
        switch (unitType) {
            case VOLUME:
                return VolumetricEnergyDensityUnit.values();
            case MASS:
                return GravimetricEnergyDensityUnit.values();
            default:
                throw new IllegalArgumentException();
        }
    }

    @NonNull
    public static String getString(@NonNull EnergyDensityUnit unit) {
        return unit.getAmountUnitType().name() + "@" + unit.name();
    }

    @NonNull
    public static EnergyDensityUnit fromString(@NonNull String string) {
        String[] split = string.split("@");
        if (split.length != 2) throw new IllegalArgumentException();
        AmountUnitType unitType = AmountUnitType.valueOf(split[0]);
        EnergyDensityUnit[] units = (EnergyDensityUnit[]) getUnits(unitType);
        for (EnergyDensityUnit unit : units) {
            if (unit.name().equals(split[1])) return unit;
        }
        throw new IllegalArgumentException();
    }

    /**
     * @return returns Energy density of provided unit and string representation of big decimal value
     * or 0 if string is not in correct format
     */
    @NonNull
    public static EnergyDensity getOrZero(@NonNull EnergyDensityUnit unit, @NonNull String energyValue) {
        BigDecimal bigDecimal;
        try {
            bigDecimal = new BigDecimal(energyValue);
        } catch (NumberFormatException e) {
            if (BuildConfig.DEBUG) Timber.w("Value: '%s' is not valid BigInteger", energyValue);
            bigDecimal = BigDecimal.ZERO;
        }
        return new EnergyDensity(unit, bigDecimal);
    }

    @NonNull
    static BigDecimal convertValue(@NonNull BigDecimal value, @NonNull EnergyDensityUnit source, @NonNull EnergyDensityUnit target) {
        BigDecimal multiply = value.multiply(source.getEnergyUnit().getInKJ()).multiply(target.getAmountBase());

        BigDecimal divide = multiply.divide(target.getEnergyUnit().getInKJ().multiply(source.getAmountBase()), MathContext.DECIMAL64);
        return divide.stripTrailingZeros();
    }
}
