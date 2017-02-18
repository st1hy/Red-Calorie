package com.github.st1hy.countthemcalories.activities.overview.model;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

public class DayData {

    @NonNull
    private final DateTime dateTime;
    private final float value;
    private boolean hasAnyData;


    public DayData(@NonNull DateTime dateTime, float value, boolean hasAnyData) {
        this.dateTime = dateTime;
        this.value = value;
        this.hasAnyData = hasAnyData;
    }

    @NonNull
    public DateTime getDateTime() {
        return dateTime;
    }

    public float getValue() {
        return value;
    }

    public boolean hasAnyData() {
        return hasAnyData;
    }

    public boolean isToday() {
        DateTime now = DateTime.now();
        DateTime today = now.withTimeAtStartOfDay();
        DateTime tomorrow = now.plusDays(1).withTimeAtStartOfDay();
        return dateTime.isEqual(today) || dateTime.isAfter(today) && dateTime.isBefore(tomorrow);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayData dayData = (DayData) o;

        if (Float.compare(dayData.value, value) != 0) return false;
        if (hasAnyData != dayData.hasAnyData) return false;
        return dateTime.equals(dayData.dateTime);

    }

    @Override
    public int hashCode() {
        int result = dateTime.hashCode();
        result = 31 * result + (value != +0.0f ? Float.floatToIntBits(value) : 0);
        result = 31 * result + (hasAnyData ? 1 : 0);
        return result;
    }
}