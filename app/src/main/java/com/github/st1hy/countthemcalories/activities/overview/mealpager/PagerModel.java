package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.overview.model.DayData;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriod;
import com.github.st1hy.countthemcalories.core.rx.Functions;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.List;

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
    private final transient PublishSubject<TimePeriod> timePeriodDatesPublishSubject = PublishSubject.create();
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
        boolean datesChanged = datesChanged(this.timePeriod, timePeriod);
        if (anyChange) {
            setTimePeriod(timePeriod);
            timePeriodChangesSubject.onNext(timePeriod);
        }
        if (datesChanged) {
            timePeriodDatesPublishSubject.onNext(timePeriod);
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

    @NonNull
    @CheckResult
    public Observable<TimePeriod> timePeriodDatesChanges() {
        return timePeriodChangesSubject.asObservable();
    }

    public Observable<Integer> getSelectedPageChanges() {
        return selectedPageChanges.asObservable();
    }

    private static boolean datesChanged(TimePeriod first, TimePeriod second) {
        if (first == null || second == null) return true;
        List<DayData> days1 = first.getData();
        List<DayData> days2 = second.getData();
        if (days1.size() != days2.size()) return true;
        int size = days1.size();
        for (int i = 0; i < size; i++) {
            DayData day1 = days1.get(i);
            DayData day2 = days2.get(i);
            if (!day1.isDay(day2.getDateTime())) {
                return true;
            }
        }
        return false;
    }
}
