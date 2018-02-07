package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;

public interface AmountUnit extends Unit {

    @NonNull
    AmountUnitType getType();

    /**
     * Sometimes units are not singular (ie 100g) and what makes sense in energy density (kcal/100g)
     * doesn't when typing specific amount: 2.5 100 g should be 250 g, we must convert those units back
     * to singular when typing mass. This value describes this relation.
     *
     * @return base singular unit or this if unit is already singular
     */
    @NonNull
    AmountUnit getBaseUnit();
}
