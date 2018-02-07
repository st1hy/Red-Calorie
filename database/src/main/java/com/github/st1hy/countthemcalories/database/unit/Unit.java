package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.StringRes;

public interface Unit {

    /**
     * Base for conversion between database format and this unit format.
     */
    double getBase();

    @StringRes
    int getNameRes();

}
