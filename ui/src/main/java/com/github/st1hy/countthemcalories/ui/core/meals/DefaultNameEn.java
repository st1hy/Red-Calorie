package com.github.st1hy.countthemcalories.ui.core.meals;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.R;

import org.joda.time.DateTime;

import javax.inject.Inject;

public class DefaultNameEn implements DefaultNameSelector {

    @Inject
    public DefaultNameEn() {
    }

    @Override
    public int matchDate(@NonNull DateTime time) {
        DateTime h2 = time.withTime(2, 0, 0, 0);
        if (time.isBefore(h2)) {
            return R.string.add_meal_name_dinner;
        }
        if (time.isBefore(time.withTime(10, 0, 0, 0))) {
            return R.string.add_meal_name_breakfast;
        } else if (time.isBefore(time.withTime(15, 0, 0, 0))) {
            return R.string.add_meal_name_lunch;
        } else {
            return R.string.add_meal_name_dinner;
        }
    }

}
