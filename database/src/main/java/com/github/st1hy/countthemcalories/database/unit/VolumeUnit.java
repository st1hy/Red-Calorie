package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.database.R;

import java.math.BigDecimal;

public enum VolumeUnit implements AmountUnit {
    ML(1, BigDecimal.ONE, R.string.unit_milliliter) {
        @NonNull
        @Override
        public AmountUnit getBaseUnit() {
            return this;
        }
    },
    ML100(2, BigDecimal.valueOf(100, 0), R.string.unit_100_milliliter) {
        @NonNull
        @Override
        public AmountUnit getBaseUnit() {
            return ML;
        }
    },
    FL_OZ(3, BigDecimal.valueOf(295735295625L, 10), R.string.unit_fl_oz) {
        @NonNull
        @Override
        public AmountUnit getBaseUnit() {
            return FL_OZ;
        }
    };

    private final int id;
    private final BigDecimal inML;
    private final int nameRes;

    VolumeUnit(int id, @NonNull BigDecimal inML, int nameRes) {
        this.id = id;
        this.inML = inML;
        this.nameRes = nameRes;
    }

    /**
     * @return base value of one converted into ml
     */
    @NonNull
    public BigDecimal getBase() {
        return inML;
    }

    @StringRes
    public int getNameRes() {
        return nameRes;
    }

    @NonNull
    @Override
    public AmountUnitType getType() {
        return AmountUnitType.VOLUME;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public static VolumeUnit fromId(int id) {
        switch (id) {
            case 1: return ML;
            case 2: return ML100;
            case 3: return FL_OZ;
        }
        return null;
    }
}
