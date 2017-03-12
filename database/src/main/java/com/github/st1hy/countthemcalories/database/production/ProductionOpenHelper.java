package com.github.st1hy.countthemcalories.database.production;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.st1hy.countthemcalories.database.DaoMaster;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ProductionOpenHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = DaoMaster.SCHEMA_VERSION;

    @Inject
    public ProductionOpenHelper(@Named("appContext") Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        db.beginTransaction();
        db.setTransactionSuccessful();
    }
}
