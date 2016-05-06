package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.database.R;

public enum FoodAmountUnit {
    G(AmountUnitType.MASS, R.string.format_gram),
    ML(AmountUnitType.VOLUME, R.string.format_milliliter)
    ;
    private final AmountUnitType type;
    private final int formatResId;

    FoodAmountUnit(@NonNull AmountUnitType type, @StringRes int formatResId) {
        this.type = type;
        this.formatResId = formatResId;
    }

    @NonNull
    public AmountUnitType getAmountUnitType() {
        return type;
    }

    @PluralsRes
    public int getFormatResId() {
        return formatResId;
    }
}
