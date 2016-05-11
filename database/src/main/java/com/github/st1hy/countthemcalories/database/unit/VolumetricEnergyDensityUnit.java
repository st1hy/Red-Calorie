package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.database.R;

import java.math.BigDecimal;

import static com.github.st1hy.countthemcalories.database.unit.AmountUnitType.VOLUME;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.HUNDRED;
import static com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils.ONE;
import static com.github.st1hy.countthemcalories.database.unit.EnergyUnit.KCAL;
import static com.github.st1hy.countthemcalories.database.unit.EnergyUnit.KJ;

public enum VolumetricEnergyDensityUnit implements EnergyDensityUnit {
    KCAL_AT_100ML(1, R.string.format_kcal_at_100ml, R.string.unit_kcal_at_100ml, HUNDRED, KCAL),
    KJ_AT_100ML(2, R.string.format_kj_at_100ml, R.string.unit_kj_at_100ml, HUNDRED, KJ),
    KCAL_AT_ML(3, R.string.format_kcal_at_1ml, R.string.unit_kcal_at_1ml, ONE, KCAL),
    KJ_AT_ML(4, R.string.format_kj_at_1ml, R.string.unit_kj_at_1ml, ONE, KJ);

    final int id; //keep constant
    final int formatResId;
    final int nameResId;
    final BigDecimal amountBase;
    final EnergyUnit energyUnit;

    VolumetricEnergyDensityUnit(int id, @StringRes int formatResId, @StringRes int nameResId,
                                @NonNull BigDecimal amountBase, @NonNull EnergyUnit energyUnit) {
        this.id = id;
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
    @Override
    public AmountUnitType getAmountUnitType() {
        return VOLUME;
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
        if (!(unit instanceof VolumetricEnergyDensityUnit))
            throw new IllegalArgumentException("Conversion only supported for the same unit type");
        return EnergyDensityUtils.convertValue(value, this, unit);
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
