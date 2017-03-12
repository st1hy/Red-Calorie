package com.github.st1hy.countthemcalories.database.property;

import android.support.annotation.NonNull;

public enum CreationSource {
    GENERATED(0), USER(1);

    private final int databaseValue;

    CreationSource(int databaseValue) {
        this.databaseValue = databaseValue;
    }

    public int getDatabaseValue() {
        return databaseValue;
    }

    public boolean isUserValue() {
        return this == USER;
    }

    @NonNull
    public static CreationSource fromDatabaseValue(int databaseValue) {
        CreationSource[] values = CreationSource.values();
        for (CreationSource value : values) {
            if (value.databaseValue == databaseValue) {
                return value;
            }
        }
        throw new IllegalArgumentException("Database value does not match legal source value");
    }
}
