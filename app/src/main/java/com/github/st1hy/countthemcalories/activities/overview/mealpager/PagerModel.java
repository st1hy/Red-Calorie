package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.model.DayData;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriodModel;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.internal.DoubleCheck;
import rx.Observable;

@PerActivity
public class PagerModel {

    @Inject
    MealPagerView view;
    @Inject
    TimePeriodModel model;

    private final Provider<Observable<DayData>> selectedDay = DoubleCheck.provider(
            () -> Observable.combineLatest(view.onPageSelected(),
                    model.mostRecent(), (page, period) -> period.getDayDataAt(page))
                    .share()
    );

    @Inject
    public PagerModel() {
    }

    @NonNull
    @CheckResult
    public Observable<DayData> selectedDay() {
        return selectedDay.get();
    }
}
