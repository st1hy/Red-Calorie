package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;

public enum AmountUnitType {
    VOLUME(1),
    MASS(2);
    private final int id;

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
     * @return {@link AmountUnitType} matching provided id
     * @throws IllegalArgumentException if id does not match any units
     */
    @NonNull
    public static AmountUnitType fromId(int id) {
        switch (id) {
            case 1:
                return VOLUME;
            case 2:
                return MASS;
            default:
                throw new IllegalArgumentException("Cannot find unit matching this id");
        }
    }
}
