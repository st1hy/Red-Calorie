package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.database.R;

import java.math.BigDecimal;

public enum MassUnit implements AmountUnit {
    G(1, BigDecimal.ONE, R.string.unit_gram),
    G100(2, BigDecimal.valueOf(100, 0), R.string.unit_100_gram);

    private final int id;
    private final BigDecimal inG;
    private final int nameRes;

    MassUnit(int id, @NonNull BigDecimal inG, int nameRes) {
        this.id = id;
        this.inG = inG;
        this.nameRes = nameRes;
    }

    /**
     * @return base value of one converted into g
     */
    @NonNull
    public BigDecimal getBase() {
        return inG;
    }

    @StringRes
    public int getNameRes() {
        return nameRes;
    }

    @NonNull
    @Override
    public AmountUnitType getType() {
        return AmountUnitType.MASS;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public static MassUnit fromId(int id) {
        switch (id) {
            case 1: return G;
            case 2: return G100;
        }
        return null;
    }
}
