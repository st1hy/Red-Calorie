package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;

import com.github.st1hy.countthemcalories.database.R;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.MASS;

public enum GravimetricEnergyDensityUnit implements EnergyDensityUnit {
    KCAL_AT_100G(R.plurals.unit_kcal_at_100g, EnergyDensityUtils.KJ_AT_G_IN_KCAL_AT_100_GRAM),
    KJ_AT_100G(R.plurals.unit_kj_at_100g, EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_100_G),
    KCAL_AT_G(R.plurals.unit_kcal_at_1g, EnergyDensityUtils.KJ_AT_GRAM_IN_KCAL_AT_GRAM),
    KJ_AT_G(R.plurals.unit_kj_at_1g, EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_G);

    final int pluralResId;
    final BigDecimal conversionRate;

    GravimetricEnergyDensityUnit(@PluralsRes int pluralResId, BigDecimal conversionRate) {
        this.pluralResId = pluralResId;
        this.conversionRate = conversionRate;
    }

    @PluralsRes
    public int getPluralResId() {
        return pluralResId;
    }

    @NonNull
    public AmountUnitType getAmountUnitType() {
        return MASS;
    }

    @NonNull
    @Override
    public BigDecimal convertValue(@NonNull BigDecimal value, @NonNull EnergyDensityUnit unit) {
        if (this == unit) return value;
        GravimetricEnergyDensityUnit target = (GravimetricEnergyDensityUnit) unit;
        BigDecimal multiply = value.multiply(conversionRate);
        BigDecimal divide = multiply.divide(target.conversionRate, MathContext.DECIMAL32);
        return divide.stripTrailingZeros();
    }
}
