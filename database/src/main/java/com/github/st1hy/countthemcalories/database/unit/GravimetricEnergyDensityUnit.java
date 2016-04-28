package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;

import com.github.st1hy.countthemcalories.database.R;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.MASS;

public enum GravimetricEnergyDensityUnit implements EnergyDensityUnit {
    KCAL_AT_100G(1,R.plurals.unit_kcal_at_100g, EnergyDensityUtils.KJ_AT_G_IN_KCAL_AT_100_GRAM),
    KJ_AT_100G(2,R.plurals.unit_kj_at_100g, EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_100_G),
    KCAL_AT_G(3,R.plurals.unit_kcal_at_1g, EnergyDensityUtils.KJ_AT_GRAM_IN_KCAL_AT_GRAM),
    KJ_AT_G(4,R.plurals.unit_kj_at_1g, EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_G);

    final int itemId; //keep constant
    final int pluralResId;
    final BigDecimal conversionRate;

    GravimetricEnergyDensityUnit(int itemId, @PluralsRes int pluralResId, BigDecimal conversionRate) {
        this.itemId = itemId;
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

    @Override
    public int getId() {
        return itemId;
    }

    @NonNull
    public static GravimetricEnergyDensityUnit fromId(int id) {
        for (GravimetricEnergyDensityUnit unit : values()) {
            if (unit.getId() == id) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown id");
    }
}
