package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;

import java.math.BigDecimal;

public interface EnergyDensityUnit {

    @PluralsRes
    int getPluralResId();

    @NonNull
    AmountUnitType getAmountUnitType();

    @NonNull
    BigDecimal convertValue(@NonNull BigDecimal value, @NonNull EnergyDensityUnit unit);

    int getId();
}
