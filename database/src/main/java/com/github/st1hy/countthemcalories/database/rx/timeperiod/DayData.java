package com.github.st1hy.countthemcalories.database.rx.timeperiod;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class DayData {

    @NonNull
    private final DateTime dateTime;
    private final float value;
    private final float weight;
    private boolean hasAnyData;

    @ParcelConstructor
    DayData(@NonNull DateTime dateTime, float value, float weight, boolean hasAnyData) {
        this.dateTime = dateTime;
        this.value = value;
        this.weight = weight;
        this.hasAnyData = hasAnyData;
    }

    @NonNull
    public DateTime getDateTime() {
        return dateTime;
    }

    public float getValue() {
        return value;
    }

    public float getWeight() {
        return weight;
    }

    public boolean getHasAnyData() {
        return hasAnyData;
    }

    public boolean isToday() {
        return isDay(DateTime.now());
    }

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

        return Float.compare(dayData.value, value) == 0
                && Float.compare(dayData.weight, weight) == 0
                && hasAnyData == dayData.hasAnyData
                && dateTime.equals(dayData.dateTime);
    }

    @Override
    public int hashCode() {
        int result = dateTime.hashCode();
        result = 31 * result + (value != +0.0f ? Float.floatToIntBits(value) : 0);
        result = 31 * result + (weight != +0.0f ? Float.floatToIntBits(weight) : 0);
        result = 31 * result + (hasAnyData ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DayData{" +
                "dateTime=" + dateTime +
                ", value=" + value +
                ", weight=" + weight +
                ", hasAnyData=" + hasAnyData +
                '}';
    }
}
