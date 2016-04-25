package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;

import java.math.BigDecimal;

public class EnergyDensityUtils {
    public static final BigDecimal KJ_AT_GRAM_IN_KCAL_AT_GRAM = BigDecimal.valueOf(4184, 3);
    public static final BigDecimal KJ_AT_G_IN_KCAL_AT_100_GRAM = BigDecimal.valueOf(4184, 5);
    public static final BigDecimal KJ_AT_G_IN_KJ_AT_G = BigDecimal.ONE;
    public static final BigDecimal KJ_AT_G_IN_KJ_AT_100_G = BigDecimal.valueOf(1, 2);

    public static EnergyDensityUnit[] getUnits(@NonNull AmountUnitType unitType) {
        switch (unitType) {
            case VOLUME:
                return VolumetricEnergyDensityUnit.values();
            case MASS:
                return GravimetricEnergyDensityUnit.values();
            default:
                throw new IllegalArgumentException();
        }
    }
}
