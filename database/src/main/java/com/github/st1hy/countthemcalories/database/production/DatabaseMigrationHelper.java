package com.github.st1hy.countthemcalories.database.production;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

public class DatabaseMigrationHelper {

    /**
     * @return true if migration ended successfully
     */
    public boolean onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        return false;
    }

    /**
     * @return true if migration ended successfully
     */
    public boolean onDowngrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        return false;
    }
}
