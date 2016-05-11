package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.database.R;

import java.math.BigDecimal;

import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.MASS;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.HUNDRED;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.ONE;
import static com.github.st1hy.countthemcalories.database.unit.EnergyUnit.KCAL;
import static com.github.st1hy.countthemcalories.database.unit.EnergyUnit.KJ;

public enum GravimetricEnergyDensityUnit implements EnergyDensityUnit {
    KCAL_AT_100G(1,R.string.format_kcal_at_100g,R.string.unit_kcal_at_100g, HUNDRED, KCAL),
    KJ_AT_100G(2,R.string.format_kj_at_100g, R.string.unit_kj_at_100g, HUNDRED, KJ),
    KCAL_AT_G(3,R.string.format_kcal_at_1g, R.string.unit_kcal_at_1g, ONE, KCAL),
    KJ_AT_G(4,R.string.format_kj_at_1g, R.string.unit_kj_at_1g, ONE, KJ);


    final int itemId; //keep constant
    final int formatResId;
    final BigDecimal amountBase;
    final EnergyUnit energyUnit;
    final int nameResId;

    GravimetricEnergyDensityUnit(int itemId, @StringRes int formatResId, @StringRes int nameResId,
                                 @NonNull BigDecimal amountBase, @NonNull EnergyUnit energyUnit) {
        this.itemId = itemId;
        this.formatResId = formatResId;
        this.nameResId = nameResId;
        this.amountBase = amountBase;
        this.energyUnit = energyUnit;
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
    public EnergyUnit getEnergyUnit() {
        return energyUnit;
    }

    @NonNull
    @Override
    public BigDecimal getAmountBase() {
        return amountBase;
    }

    @NonNull
    @Override
    public BigDecimal convertValue(@NonNull BigDecimal value, @NonNull EnergyDensityUnit unit) {
        if (this == unit) return value;
        if (!(unit instanceof  GravimetricEnergyDensityUnit))
            throw new IllegalArgumentException("Conversion only supported for the same unit type");
        return EnergyDensityUtils.convertValue(value, this, unit);
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
