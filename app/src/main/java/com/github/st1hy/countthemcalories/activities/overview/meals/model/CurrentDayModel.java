package com.github.st1hy.countthemcalories.activities.overview.meals.model;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.mealpager.PagerModel;
import com.github.st1hy.countthemcalories.activities.overview.meals.inject.MealPagerPosition;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import org.joda.time.DateTime;

import javax.inject.Inject;

import rx.Observable;

@PerFragment
public class CurrentDayModel {

    @MealPagerPosition
    private final int currentPosition;
    @Inject
    PagerModel model;

    @Inject
    public CurrentDayModel(@MealPagerPosition int currentPosition) {
        this.currentPosition = currentPosition;
    }

    @NonNull
    @CheckResult
    public Observable<DateTime> getCurrentDay() {
        return model.dateAtPosition(currentPosition);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}
