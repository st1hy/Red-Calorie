package com.github.st1hy.countthemcalories.activities.overview.graph.model;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.database.property.AmountUnitTypePropertyConverter;
import com.github.st1hy.countthemcalories.database.property.BigDecimalPropertyConverter;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

public class GraphTimePeriod {

    private static final int POSITION_TIME = 0;
    private static final int POSITION_AMOUNT = 1;
    private static final int POSITION_ENERGY_DENSITY = 2;
    private static final int POSITION_AMOUNT_TYPE = 3;

    private final int daysCount;
    private final SparseArray<DayData> data;
    private final float min, max, average, median;

    private GraphTimePeriod(@NonNull Builder builder) {
        this.daysCount = builder.daysCount;
        this.data = builder.data;
        this.min = builder.min;
        this.max = builder.max;
        this.average = builder.average;
        this.median = builder.median;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getAverage() {
        return average;
    }

    public float getMedian() {
        return median;
    }

    public int getCount() {
        return daysCount;
    }

    @NonNull
    public DayData getDayDataAt(int position) {
        return checkNotNull(data.get(position));
    }

    public static class Builder {
        private final Cursor cursor;
        private final PhysicalQuantitiesModel quantitiesModel;
        private final DateTime start;
        private final int daysCount;

        private final SparseArray<DayData> data;
        private boolean loaded = false;
        private float min = Float.MAX_VALUE, max = Float.MIN_VALUE, average = 0f, median = 0f;
        private final float[] medianData;
        private int notZeroDays = 0;

        public Builder(@NonNull Cursor cursor,
                       @NonNull PhysicalQuantitiesModel quantitiesModel,
                       @NonNull DateTime start,
                       @NonNull DateTime end) {
            this.cursor = cursor;
            this.quantitiesModel = quantitiesModel;
            this.start = start;
            this.daysCount = countDays(start, end);
            this.data = new SparseArray<>(daysCount);
            this.medianData = new float[daysCount];
        }

        @NonNull
        public GraphTimePeriod build() {
            if (!loaded) {
                loadAll();
                loaded = true;
            }
            return new GraphTimePeriod(this);
        }

        private void loadAll() {
            boolean positionLoadedButNotUsed = false;
            for (int i = 0; i < daysCount; i++) {
                DateTime start = this.start.plusDays(i);
                DateTime end = this.start.plusDays(i + 1);
                float amount = 0f;
                boolean hasAnyData = false;
                while (positionLoadedButNotUsed || cursor.moveToNext()) {
                    positionLoadedButNotUsed = true;
                    long timestamp = cursor.getLong(POSITION_TIME);
                    DateTime time = new DateTime(timestamp);
                    if (time.isAfter(end)) {
                        break;
                    }
                    positionLoadedButNotUsed = false;
                    hasAnyData = true;
                    amount += getAmount();
                }
                DayData dayData = new DayData(start, amount, hasAnyData);
                updateStatistics(dayData, i);
                data.put(i, dayData);
            }
            computeAverageAndMedian();
        }

        private void updateStatistics(@NonNull DayData data, int i) {
            float value = data.getValue();
            if (value < min) min = value;
            if (value > max) max = value;
            if (value > 0) {
                average += value;
                medianData[notZeroDays] = value;
                notZeroDays++;
            }
        }

        private void computeAverageAndMedian() {
            if (notZeroDays > 0) {
                average /= notZeroDays;
                Arrays.sort(medianData, 0, notZeroDays);
                median = median(medianData, 0, notZeroDays);
            }
        }

        private float getAmount() {
            BigDecimal energyDensityValue = BigDecimalPropertyConverter.INSTANCE
                    .convertToEntityProperty(cursor.getString(POSITION_ENERGY_DENSITY));
            AmountUnitType amountUnitType = AmountUnitTypePropertyConverter.INSTANCE
                    .convertToEntityProperty(cursor.getInt(POSITION_AMOUNT_TYPE));
            if (energyDensityValue == null) energyDensityValue = BigDecimal.ZERO;

            EnergyDensity databaseEnergyDensity = EnergyDensity.fromDatabaseValue(amountUnitType, energyDensityValue);
            EnergyDensity energyDensity = quantitiesModel.convertToPreferred(databaseEnergyDensity);

            BigDecimal currentAmount = BigDecimalPropertyConverter.INSTANCE
                    .convertToEntityProperty(cursor.getString(POSITION_AMOUNT));
            if (currentAmount == null) currentAmount = BigDecimal.ZERO;
            AmountUnit amountUnit = EnergyDensityUtils.getDefaultAmountUnit(amountUnitType);
            return quantitiesModel.getEnergyAmountFrom(currentAmount, amountUnit, energyDensity).floatValue();
        }
    }

    private static int countDays(@NonNull DateTime start,
                                 @NonNull DateTime end) {
        return Days.daysBetween(start, end).getDays();
    }


    public static float median(float[] m) {
        return median(m, 0, m.length);
    }

    public static float median(float[] m, int from, int to) {
        if (from > to || from < 0 || to < 0) throw new IllegalArgumentException("Incorrect range");
        int length = to - from;
        if (length == 0) return 0f;
        if (length == 1) return m[from];
        int middle = from + length / 2;
        if (length % 2 == 1) {
            return m[middle];
        } else {
            return (m[middle - 1] + m[middle]) / 2.0f;
        }
    }

}
