package com.github.st1hy.countthemcalories.activities.overview.model;

import android.support.annotation.NonNull;

public class AutoScaleLines {

    private final TimePeriod timePeriod;

    public AutoScaleLines(@NonNull TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public float[] sensibleScale(float minViewSize, float totalViewSize) {
        float maxValue = timePeriod.maxDisplayValue();
        int size = (int) Math.floor(totalViewSize / minViewSize);
        float delta = minViewSize / totalViewSize;
        float[] values = new float[size];
        for (int i = 0; i < size; i++) {
            values[i] = (i + 1) * delta * maxValue;
        }
        return values;
    }
}
