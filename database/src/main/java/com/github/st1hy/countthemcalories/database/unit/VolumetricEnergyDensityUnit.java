package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;

import com.github.st1hy.countthemcalories.database.R;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.VOLUME;

public enum VolumetricEnergyDensityUnit implements EnergyDensityUnit {
    KCAL_AT_100ML(R.plurals.unit_kcal_at_100ml, EnergyDensityUtils.KJ_AT_G_IN_KCAL_AT_100_GRAM),
    KJ_AT_100ML(R.plurals.unit_kj_at_100ml, EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_100_G),
    KCAL_AT_ML(R.plurals.unit_kcal_at_1ml, EnergyDensityUtils.KJ_AT_GRAM_IN_KCAL_AT_GRAM),
    KJ_AT_ML(R.plurals.unit_kj_at_1ml, EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_G);

    private final int pluralResId;
    final BigDecimal conversionRate;

    VolumetricEnergyDensityUnit(@PluralsRes int pluralResId, BigDecimal conversionRate) {
        this.pluralResId = pluralResId;
        this.conversionRate = conversionRate;
    }

    @PluralsRes
    public int getPluralResId() {
        return pluralResId;
    }

    @NonNull
    public AmountUnitType getAmountUnitType() {
        return VOLUME;
    }

    @NonNull
    @Override
    public BigDecimal convertValue(@NonNull BigDecimal value, @NonNull EnergyDensityUnit unit) {
        if (this == unit) return value;
        VolumetricEnergyDensityUnit target = (VolumetricEnergyDensityUnit) unit;
        BigDecimal multiply = value.multiply(conversionRate);
        BigDecimal divide = multiply.divide(target.conversionRate, MathContext.DECIMAL32);
        return divide.stripTrailingZeros();
    }
}
