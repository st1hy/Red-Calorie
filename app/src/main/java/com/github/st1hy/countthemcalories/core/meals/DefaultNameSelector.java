package com.github.st1hy.countthemcalories.core.meals;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import org.joda.time.DateTime;

public interface DefaultNameSelector {

    @StringRes
    int matchDate(@NonNull DateTime dateTime);
}
