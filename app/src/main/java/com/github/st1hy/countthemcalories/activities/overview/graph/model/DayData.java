package com.github.st1hy.countthemcalories.activities.overview.graph.model;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class DayData {

    @NonNull
    private final DateTime dateTime;
    @NonNull
    private final BigDecimal value;
    private boolean hasAnyData;

    public DayData(@NonNull DateTime dateTime, @NonNull BigDecimal value, boolean hasAnyData) {
        this.dateTime = dateTime;
        this.value = value;
        this.hasAnyData = hasAnyData;
    }

    @NonNull
    public DateTime getDateTime() {
        return dateTime;
    }

    @NonNull
    public BigDecimal getValue() {
        return value;
    }

    public boolean hasAnyData() {
        return hasAnyData;
    }
}
