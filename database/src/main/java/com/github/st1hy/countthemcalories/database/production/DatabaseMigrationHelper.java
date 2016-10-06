package com.github.st1hy.countthemcalories.database.production;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.database.Database;

public class DatabaseMigrationHelper {

    /**
     * @return true if migration ended successfully
     */
    public boolean onUpgrade(@NonNull Database db, int oldVersion, int newVersion) {
        return false;
    }

    /**
     * @return true if migration ended successfully
     */
    public boolean onDowngrade(@NonNull Database db, int oldVersion, int newVersion) {
        return false;
    }
}
