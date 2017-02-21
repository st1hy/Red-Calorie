package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.overview.model.DayData;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriod;
import com.github.st1hy.countthemcalories.core.rx.Functions;

import org.joda.time.DateTime;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import rx.Observable;
import rx.subjects.PublishSubject;

@Parcel(Parcel.Serialization.BEAN)
public class PagerModel {

    @Nullable
    private DayData selectedDay;
    @Nullable
    private TimePeriod timePeriod;
    private int selectedPage = -1;

    private final transient PublishSubject<TimePeriod> timePeriodChangesSubject = PublishSubject.create();
    private final transient PublishSubject<Integer> selectedPageChanges = PublishSubject.create();

    @ParcelConstructor
    public PagerModel() {
    }

    public void setLatestDay(@NonNull DayData dayData) {
        this.selectedDay = dayData;
    }

    @Nullable
    public DayData getSelectedDay() {
        return selectedDay;
    }

    public int getSelectedPage() {
        return selectedPage;
    }

    public void setSelectedPage(int selectedPage) {
        boolean pageChanges = selectedPage != this.selectedPage;
        this.selectedPage = selectedPage;
        if (pageChanges) {
            selectedPageChanges.onNext(selectedPage);
        }
    }

    @Nullable
    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(@NonNull TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public boolean updateModel(TimePeriod timePeriod) {
        boolean anyChange = !timePeriod.equals(this.timePeriod);
        if (anyChange) {
            setTimePeriod(timePeriod);
            timePeriodChangesSubject.onNext(timePeriod);
        }
        return anyChange;
    }

    @NonNull
    @CheckResult
    public Observable<TimePeriod> timePeriodChanges() {
        return timePeriodChangesSubject.asObservable();
    }

    @NonNull
    @CheckResult
    public Observable<TimePeriod> timePeriodMostRecent() {
        return Observable.just(timePeriod).filter(Functions.NOT_NULL)
                .mergeWith(timePeriodChangesSubject);
    }

    public Observable<Integer> getSelectedPageChanges() {
        return selectedPageChanges.asObservable();
    }

    @NonNull
    @CheckResult
    public Observable<DateTime> dateAtPosition(int currentPosition) {
        return timePeriodMostRecent().map(timePeriod -> timePeriod.getDayDataAt(currentPosition))
                .map(DayData::getDateTime);
    }
}
