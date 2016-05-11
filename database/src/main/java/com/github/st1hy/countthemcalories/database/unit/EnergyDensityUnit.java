package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.math.BigDecimal;

public interface EnergyDensityUnit {

    @StringRes
    int getFormatResId();

    @StringRes
    int getNameResId();

    @NonNull
    AmountUnitType getAmountUnitType();

    @NonNull
    EnergyUnit getEnergyUnit();

    @NonNull
    BigDecimal getAmountBase();

    @NonNull
    BigDecimal convertValue(@NonNull BigDecimal value, @NonNull EnergyDensityUnit unit);

    int getId();

    String name();
}
