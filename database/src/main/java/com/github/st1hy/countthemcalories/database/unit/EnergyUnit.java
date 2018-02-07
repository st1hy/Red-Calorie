package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.database.R;

public enum EnergyUnit implements Unit {
    KJ(1, 1.0, R.string.unit_kj),
    KCAL(2, 4.184, R.string.unit_kcal);

    private final int id;
    private final double inKJ;
    private final int nameRes;

    EnergyUnit(int id, double inKJ, int nameRes) {
        this.id = id;
        this.inKJ = inKJ;
        this.nameRes = nameRes;
    }

    /**
     * @return base value of one converted into kJ
     */
    public double getBase() {
        return inKJ;
    }

    @StringRes
    public int getNameRes() {
        return nameRes;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public static EnergyUnit fromId(int id) {
        switch (id) {
            case 1:
                return KJ;
            case 2:
                return KCAL;
        }
        return null;
    }
}
