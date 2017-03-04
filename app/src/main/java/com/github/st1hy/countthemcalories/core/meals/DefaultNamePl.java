package com.github.st1hy.countthemcalories.core.meals;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;

import org.joda.time.DateTime;

import javax.inject.Inject;

public class DefaultNamePl implements DefaultNameSelector {

    @Inject
    public DefaultNamePl() {
    }

    @Override
    public int matchDate(@NonNull DateTime time) {
        if (time.isBefore(time.withTime(2, 0, 0, 0))) {
            return R.string.add_meal_name_supper;
        }
        if (time.isBefore(time.withTime(11, 0, 0, 0))) {
            return R.string.add_meal_name_breakfast;
        } else if (time.isBefore(time.withTime(17, 0, 0, 0))) {
            return R.string.add_meal_name_dinner;
        } else {
            return R.string.add_meal_name_supper;
        }
    }
}
