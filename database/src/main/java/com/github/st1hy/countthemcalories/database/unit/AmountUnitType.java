package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;

public enum AmountUnitType {
    UNKNOWN(-1),
    VOLUME(1),
    MASS(2);
    private final int id; //do not change existing values, mapped into database

    AmountUnitType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Find unit matching provided id
     *
     * @param id id of the unit (as in {@link #getId()}
     * @return {@link AmountUnitType} matching provided id or UNKNOWN
     */
    @NonNull
    public static AmountUnitType fromId(Integer id) {
        if (id == null) return UNKNOWN;
        switch (id) {
            case 1:
                return VOLUME;
            case 2:
                return MASS;
            default:
                return UNKNOWN;
        }
    }
}
