package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.math.BigDecimal;

public interface Unit {

    /**
     * Base for conversion between database format and this unit format.
     */
    @NonNull
    BigDecimal getBase();

    @StringRes
    int getNameRes();

}
