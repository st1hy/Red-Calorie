package com.github.st1hy.countthemcalories.database.rx.timeperiod;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.contract.model.DayStatistic;

import org.joda.time.DateTime;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class DayData implements DayStatistic {

    @NonNull
    private final DateTime dateTime;
    private final float totalCalories;
    private final float weight;
    private boolean hasAnyData;

    @ParcelConstructor
    DayData(@NonNull DateTime dateTime, float totalCalories, float weight, boolean hasAnyData) {
        this.dateTime = dateTime;
        this.totalCalories = totalCalories;
        this.weight = weight;
        this.hasAnyData = hasAnyData;
    }

    @Override
    @NonNull
    public DateTime getDateTime() {
        return dateTime;
    }

    @Override
    public float getTotalCalories() {
        return totalCalories;
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public boolean getHasAnyData() {
        return hasAnyData;
    }

    @Override
    public boolean isToday() {
        return isDay(DateTime.now());
    }

    @Override
    public boolean isDay(DateTime atDay) {
        DateTime dayStart = atDay.withTimeAtStartOfDay();
        DateTime nextDayStart = atDay.plusDays(1).withTimeAtStartOfDay();
        return dateTime.isEqual(dayStart) || dateTime.isAfter(dayStart) && dateTime.isBefore(nextDayStart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayData dayData = (DayData) o;

        return Float.compare(dayData.totalCalories, totalCalories) == 0
                && Float.compare(dayData.weight, weight) == 0
                && hasAnyData == dayData.hasAnyData
                && dateTime.equals(dayData.dateTime);
    }

    @Override
    public int hashCode() {
        int result = dateTime.hashCode();
        result = 31 * result + (totalCalories != +0.0f ? Float.floatToIntBits(totalCalories) : 0);
        result = 31 * result + (weight != +0.0f ? Float.floatToIntBits(weight) : 0);
        result = 31 * result + (hasAnyData ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DayData{" +
                "dateTime=" + dateTime +
                ", totalCalories=" + totalCalories +
                ", weight=" + weight +
                ", hasAnyData=" + hasAnyData +
                '}';
    }
}
