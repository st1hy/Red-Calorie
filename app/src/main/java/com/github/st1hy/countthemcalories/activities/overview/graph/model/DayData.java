package com.github.st1hy.countthemcalories.activities.overview.graph.model;

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
}
