package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.math.BigDecimal;

public interface Unit {

    @NonNull
    BigDecimal getBase();

    @StringRes
    int getNameRes();

}
