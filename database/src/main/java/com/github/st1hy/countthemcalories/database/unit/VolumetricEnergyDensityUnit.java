package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.database.R;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.VOLUME;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.KJ_AT_GRAM_IN_KCAL_AT_GRAM;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.KJ_AT_G_IN_KCAL_AT_100_GRAM;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_100_G;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.KJ_AT_G_IN_KJ_AT_G;

public enum VolumetricEnergyDensityUnit implements EnergyDensityUnit {
    KCAL_AT_100ML(1, R.string.format_kcal_at_100ml, R.string.unit_kcal_at_100ml, KJ_AT_G_IN_KCAL_AT_100_GRAM),
    KJ_AT_100ML(2, R.string.format_kj_at_100ml, R.string.unit_kj_at_100ml, KJ_AT_G_IN_KJ_AT_100_G),
    KCAL_AT_ML(3, R.string.format_kcal_at_1ml, R.string.unit_kcal_at_1ml, KJ_AT_GRAM_IN_KCAL_AT_GRAM),
    KJ_AT_ML(4, R.string.format_kj_at_1ml, R.string.unit_kj_at_1ml, KJ_AT_G_IN_KJ_AT_G);

    final int id; //keep constant
    final int formatResId;
    final int nameResId;
    final BigDecimal conversionRate;

    VolumetricEnergyDensityUnit(int id, @StringRes int formatResId, @StringRes int nameResId, BigDecimal conversionRate) {
        this.id = id;
        this.formatResId = formatResId;
        this.nameResId = nameResId;
        this.conversionRate = conversionRate;
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
    @Override
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

    @Override
    public int getId() {
        return id;
    }

    @NonNull
    public static VolumetricEnergyDensityUnit fromId(int id) {
        for (VolumetricEnergyDensityUnit unit : values()) {
            if (unit.getId() == id) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown id");
    }
}
