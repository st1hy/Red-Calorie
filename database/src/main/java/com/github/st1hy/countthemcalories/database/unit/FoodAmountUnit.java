package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;

import com.github.st1hy.countthemcalories.database.R;

public enum FoodAmountUnit {
    G(AmountUnitType.MASS, R.plurals.unit_gram),
    ML(AmountUnitType.VOLUME, R.plurals.unit_milliliter)
    ;
    private final AmountUnitType type;
    private final int pluralId;

    FoodAmountUnit(@NonNull AmountUnitType type, @PluralsRes int pluralId) {
        this.type = type;
        this.pluralId = pluralId;
    }

    @NonNull
    public AmountUnitType getAmountUnitType() {
        return type;
    }

    @PluralsRes
    public int getPluralResId() {
        return pluralId;
    }
}
