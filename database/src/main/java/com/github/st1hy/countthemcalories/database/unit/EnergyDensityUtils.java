package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;

import java.math.BigDecimal;

import timber.log.Timber;

public class EnergyDensityUtils {


//    @NonNull
//    public static String getString(@NonNull EnergyDensityUnit unit) {
//        return unit.getAmountUnitType().name() + "@" + unit.name();
//    }
//
//    @NonNull
//    public static EnergyDensityUnit fromString(@NonNull String string) {
//        String[] split = string.split("@");
//        if (split.length != 2) throw new IllegalArgumentException();
//        AmountUnitType unitType = AmountUnitType.valueOf(split[0]);
//        EnergyDensityUnit[] units = (EnergyDensityUnit[]) getUnits(unitType);
//        for (EnergyDensityUnit unit : units) {
//            if (unit.name().equals(split[1])) return unit;
//        }
//        throw new IllegalArgumentException();
//    }

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
}
