package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.overview.model.DayData;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriodModel;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Inject;

import rx.Observable;

@PerActivity
public class PagerModel {

    @Inject
    MealPagerView view;
    @Inject
    TimePeriodModel model;

    private DayData selectedDay;

    @Inject
    public PagerModel() {
    }

    @NonNull
    @CheckResult
    public Observable<DayData> selectedDay() {
        return Observable.combineLatest(view.onPageSelected(),
                model.mostRecent(), (page, period) -> period.getDayDataAt(page));
    }

    public void setLatestDay(@NonNull DayData dayData) {
        this.selectedDay = dayData;
    }

    @Nullable
    public DayData getSelectedDay() {
        return selectedDay;
    }
}
