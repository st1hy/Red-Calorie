package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.database.R;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.MASS;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.KJ_AT_GRAM_IN_KCAL_AT_GRAM;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.KJ_AT_G_IN_KCAL_AT_100_GRAM;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_100_G;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_G;

public enum GravimetricEnergyDensityUnit implements EnergyDensityUnit {
    KCAL_AT_100G(1,R.string.format_kcal_at_100g,R.string.unit_kcal_at_100g, KJ_AT_G_IN_KCAL_AT_100_GRAM),
    KJ_AT_100G(2,R.string.format_kj_at_100g, R.string.unit_kj_at_100g, KJ_AT_G_IN_KJ_AT_100_G),
    KCAL_AT_G(3,R.string.format_kcal_at_1g, R.string.unit_kcal_at_1g, KJ_AT_GRAM_IN_KCAL_AT_GRAM),
    KJ_AT_G(4,R.string.format_kj_at_1g, R.string.unit_kj_at_1g, KJ_AT_G_IN_KJ_AT_G);

    final int itemId; //keep constant
    final int formatResId;
    final BigDecimal conversionRate;
    final int nameResId;

    GravimetricEnergyDensityUnit(int itemId, @StringRes int formatResId, @StringRes int nameResId, BigDecimal conversionRate) {
        this.itemId = itemId;
        this.formatResId = formatResId;
        this.conversionRate = conversionRate;
        this.nameResId = nameResId;
    }

    @StringRes
    @Override
    public int getFormatResId() {
        return formatResId;
    }

    @StringRes
    @Override
    public int getNameResId() {
        return nameResId;
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

        BigDecimal divide = multiply.divide(target.conversionRate, MathContext.DECIMAL64);
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
