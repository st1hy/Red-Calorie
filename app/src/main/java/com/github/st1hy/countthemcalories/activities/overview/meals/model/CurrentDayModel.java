package com.github.st1hy.countthemcalories.activities.overview.meals.model;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class CurrentDayModel {

    private final DateTime currentDay;

    @ParcelConstructor
    public CurrentDayModel(@NonNull DateTime currentDay) {
        this.currentDay = currentDay;
    }

    @NonNull
    public DateTime getCurrentDay() {
        return currentDay;
    }
}
