package com.github.st1hy.countthemcalories.activities.overview.graph.model;

import android.database.Cursor;
import android.database.CursorWrapper;
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

public class GraphDayCursor extends CursorWrapper {

    private static final int POSITION_TIME = 0;
    private static final int POSITION_AMOUNT = 1;
    private static final int POSITION_ENERGY_DENSITY = 2;
    private static final int POSITION_AMOUNT_TYPE = 3;

    private final PhysicalQuantitiesModel quantitiesModel;
    private final DateTime start;
    private final int daysCount;

    private final SparseArray<DayData> cache;

    public GraphDayCursor(@NonNull Cursor cursor,
                          @NonNull PhysicalQuantitiesModel quantitiesModel,
                          @NonNull DateTime start,
                          @NonNull DateTime end) {
        super(cursor);
        this.quantitiesModel = quantitiesModel;
        this.start = start;
        this.daysCount = countDays(start, end);
        this.cache = new SparseArray<>(daysCount);
    }

    @Override
    public int getCount() {
        return daysCount;
    }

    @NonNull
    public DayData getDayDataAt(int position) {
        if (position >= 0 && position < daysCount) {
            DayData dayData = cache.get(position);
            if (dayData == null) {
                dayData = count(start.plusDays(position), start.plusDays(position + 1));
                cache.put(position, dayData);
            }
            return dayData;
        } else {
            throw new IllegalArgumentException("position not within bounds");
        }
    }

    @NonNull
    private DayData count(@NonNull DateTime start, @NonNull DateTime end) {
        BigDecimal amount = BigDecimal.ZERO;
        boolean hasAnyData = false;
        if (moveToFirst()) {
            do {
                long timestamp = getLong(POSITION_TIME);
                if (start.isAfter(timestamp)) continue;
                if (end.isBefore(timestamp)) break;
                hasAnyData = true;
                amount = amount.add(getAmount());
            } while (moveToNext());
        }
        return new DayData(start, amount, hasAnyData);
    }

    @NonNull
    private BigDecimal getAmount() {
        BigDecimal energyDensityValue = BigDecimalPropertyConverter.INSTANCE
                .convertToEntityProperty(getString(POSITION_ENERGY_DENSITY));
        AmountUnitType amountUnitType = AmountUnitTypePropertyConverter.INSTANCE
                .convertToEntityProperty(POSITION_AMOUNT_TYPE);
        if (energyDensityValue == null) energyDensityValue = BigDecimal.ZERO;

        EnergyDensity databaseEnergyDensity = EnergyDensity.fromDatabaseValue(amountUnitType, energyDensityValue);
        EnergyDensity energyDensity = quantitiesModel.convertToPreferred(databaseEnergyDensity);

        BigDecimal currentAmount = BigDecimalPropertyConverter.INSTANCE
                .convertToEntityProperty(getString(POSITION_AMOUNT));
        if (currentAmount == null) currentAmount = BigDecimal.ZERO;
        AmountUnit amountUnit = EnergyDensityUtils.getDefaultAmountUnit(amountUnitType);
        return quantitiesModel.getEnergyAmountFrom(currentAmount, amountUnit, energyDensity);
    }

    private static int countDays(@NonNull DateTime start,
                                 @NonNull DateTime end) {
        return Days.daysBetween(start, end).getDays();
    }

}
