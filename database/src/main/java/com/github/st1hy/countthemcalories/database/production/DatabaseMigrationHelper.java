package com.github.st1hy.countthemcalories.database.production;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.WeightDao;

import org.greenrobot.greendao.database.Database;

public class DatabaseMigrationHelper {

    /**
     * @return true if migration ended successfully
     */
    public boolean onUpgrade(@NonNull Database db, int oldVersion, int newVersion) {
        if (oldVersion == 1005 && newVersion == 1006) {
            WeightDao.createTable(db, true);
            db.execSQL("CREATE INDEX IF NOT EXISTS IDX_MEALS_CREATION_DATE ON MEALS" +
                    " (\"CREATION_DATE\" ASC);");
            return true;
        }
        return false;
    }

    /**
     * @return true if migration ended successfully
     */
    public boolean onDowngrade(@NonNull Database db, int oldVersion, int newVersion) {
        return false;
    }
}
