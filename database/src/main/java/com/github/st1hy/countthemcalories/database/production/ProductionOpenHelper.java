package com.github.st1hy.countthemcalories.database.production;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.BuildConfig;
import com.github.st1hy.countthemcalories.database.DaoMaster;

import timber.log.Timber;

public class ProductionOpenHelper extends DaoMaster.OpenHelper {
    private final DatabaseMigrationHelper databaseMigrationHelper;

    public ProductionOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                DatabaseMigrationHelper databaseMigrationHelper) {
        super(context, name, factory);
        this.databaseMigrationHelper = databaseMigrationHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        if (BuildConfig.DEBUG) Timber.i("Creating database");
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG) Timber.i("Upgrading database from %d to %d", oldVersion, newVersion);
        boolean isHandled = databaseMigrationHelper.onUpgrade(db, oldVersion, newVersion);
        if (!isHandled) {
            recreateDatabase(db);
        } else {
            if (BuildConfig.DEBUG) Timber.i("Upgrading successful");
        }
    }

    @Override
    public void onDowngrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG) Timber.i("Downgrading database from %d to %d", oldVersion, newVersion);
        boolean isHandled = databaseMigrationHelper.onDowngrade(db, oldVersion, newVersion);
        if (!isHandled) {
            recreateDatabase(db);
        } else {
            if (BuildConfig.DEBUG) Timber.i("Downgrading successful");
        }
    }

    private void recreateDatabase(@NonNull SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Timber.w("Upgrade/downgrade not handled: recreating database from scratch");
        DaoMaster.dropAllTables(db, true);
        onCreate(db);
    }
}
