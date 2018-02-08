package com.github.st1hy.countthemcalories.activities.overview.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.Weight;
import com.github.st1hy.countthemcalories.database.property.AmountUnitTypePropertyConverter;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.google.common.collect.ImmutableList;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Parcel(Parcel.Serialization.BEAN)
public class TimePeriod {

    private static final int POSITION_TIME = 0;
    private static final int POSITION_AMOUNT = 1;
    private static final int POSITION_ENERGY_DENSITY = 2;
    private static final int POSITION_AMOUNT_TYPE = 3;

    private final List<DayData> data;
    private final float min, max, average, median;
    private final float minWeight, maxWeight;

    @ParcelConstructor
    TimePeriod(List<DayData> data, float min, float max, float average,
               float median, float minWeight, float maxWeight) {
        this.data = ImmutableList.<DayData>builder().addAll(data).build();
        this.min = min;
        this.max = max;
        this.average = average;
        this.median = median;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
    }

    private TimePeriod(@NonNull Builder builder) {
        this.data = builder.data.build();
        this.min = builder.min;
        this.max = builder.max;
        this.average = builder.average;
        this.median = builder.median;
        this.minWeight = builder.minWeight;
        this.maxWeight = builder.maxWeight;
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

    public float getMinWeight() {
        return minWeight;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    public int getDaysCount() {
        return data.size();
    }

    @NonNull
    public List<DayData> getData() {
        return data;
    }

    @NonNull
    public DayData getDayDataAt(int position) {
        return data.get(position);
    }

    @Override
    public String toString() {
        return "TimePeriod{" +
                "daysCount=" + data.size() +
                ", min=" + min +
                ", max=" + max +
                ", average=" + average +
                ", median=" + median +
                '}';
    }

    public int findDayPosition(@NonNull DateTime dateTime) {
        for (int i = data.size() - 1; i >= 0; i--) {
            DayData dayData = data.get(i);
            if (dayData.isDay(dateTime)) {
                return i;
            }
        }
        return getDaysCount() - 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimePeriod that = (TimePeriod) o;

        return Float.compare(that.min, min) == 0
                && Float.compare(that.max, max) == 0
                && Float.compare(that.average, average) == 0
                && Float.compare(that.median, median) == 0
                && Float.compare(that.minWeight, minWeight) == 0
                && Float.compare(that.maxWeight, maxWeight) == 0
                && (data != null ? data.equals(that.data) : that.data == null);
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (min != +0.0f ? Float.floatToIntBits(min) : 0);
        result = 31 * result + (max != +0.0f ? Float.floatToIntBits(max) : 0);
        result = 31 * result + (average != +0.0f ? Float.floatToIntBits(average) : 0);
        result = 31 * result + (median != +0.0f ? Float.floatToIntBits(median) : 0);
        result = 31 * result + (minWeight != +0.0f ? Float.floatToIntBits(minWeight) : 0);
        result = 31 * result + (maxWeight != +0.0f ? Float.floatToIntBits(maxWeight) : 0);
        return result;
    }

    public static class Builder {
        private final Cursor cursor;
        private final DateTime start;
        private final int daysCount;
        private final List<Weight> weightList;

        private final ImmutableList.Builder<DayData> data;
        private boolean loaded = false;
        private float min = Float.MAX_VALUE, max = Float.MIN_VALUE, average = 0f, median = 0f;
        private float minWeight = Float.MAX_VALUE, maxWeight = Float.MIN_VALUE;
        private final float[] medianCaloriesData;
        private int notZeroCalorieDays = 0;

        public Builder(@NonNull Cursor cursor,
                       @NonNull DateTime start,
                       @NonNull DateTime end,
                       @NonNull List<Weight> weight) {
            this.cursor = cursor;
            this.start = start;
            this.daysCount = countDays(start, end);
            this.data = ImmutableList.builder();
            this.medianCaloriesData = new float[daysCount];
            this.weightList = weight;
        }

        @NonNull
        public TimePeriod build() {
            if (!loaded) {
                loadAll();
                loaded = true;
            }
            return new TimePeriod(this);
        }

        private boolean hasAnyData, positionLoadedButNotUsed;
        private Iterator<Weight> weightIterator;
        private Weight weight;

        private void loadAll() {
            positionLoadedButNotUsed = false;
            weight = null;
            weightIterator = weightList.iterator();
            for (int i = 0; i < daysCount; i++) {
                hasAnyData = false;
                DateTime start = this.start.plusDays(i);
                DateTime end = this.start.plusDays(i + 1);
                float amount = getAmount(end);
                float weightValue = getWeight(end);
                DayData dayData = new DayData(start, amount, weightValue, hasAnyData);
                updateStatistics(dayData, weightValue);
                data.add(dayData);
            }
            computeAverageAndMedian();
        }

        private float getAmount(DateTime end) {
            float amount = 0f;
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
            return amount;
        }

        private float getWeight(DateTime end) {
            float weightValue = 0f;
            while (weight != null || weightIterator.hasNext()) {
                if (weight == null) weight = weightIterator.next();
                DateTime measurementDate = weight.getMeasurementDate();
                if (measurementDate.isEqual(end) || measurementDate.isAfter(end)) {
                    break;
                }
                hasAnyData = true;
                weightValue = weight.getWeight();
                weight = null;
            }
            return weightValue;
        }

        private void updateStatistics(@NonNull DayData data, float weightValue) {
            float value = data.getValue();
            if (value < min) min = value;
            if (value > max) max = value;
            if (value > 0) {
                average += value;
                medianCaloriesData[notZeroCalorieDays] = value;
                notZeroCalorieDays++;
            }
            if (weightValue > 0f) {
                if (weightValue < minWeight) minWeight = weightValue;
                if (weightValue > maxWeight) maxWeight = weightValue;
            }
        }

        private void computeAverageAndMedian() {
            if (notZeroCalorieDays > 0) {
                average /= notZeroCalorieDays;
                Arrays.sort(medianCaloriesData, 0, notZeroCalorieDays);
                median = median(medianCaloriesData, 0, notZeroCalorieDays);
            }
        }

        private double getAmount() {
            double energyDensityValue = cursor.getDouble(POSITION_ENERGY_DENSITY);
            AmountUnitType amountUnitType = AmountUnitTypePropertyConverter.INSTANCE
                    .convertToEntityProperty(cursor.getInt(POSITION_AMOUNT_TYPE));
            EnergyDensity databaseEnergyDensity = EnergyDensity.fromDatabaseValue(amountUnitType, energyDensityValue);
            double currentAmount = cursor.getDouble(POSITION_AMOUNT);
            return databaseEnergyDensity.getValue() * currentAmount;
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
