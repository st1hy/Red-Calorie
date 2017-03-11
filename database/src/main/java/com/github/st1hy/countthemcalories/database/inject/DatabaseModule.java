package com.github.st1hy.countthemcalories.database.inject;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.DaoMaster;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.production.ProductionOpenHelper;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class DatabaseModule {

    @Binds
    public abstract SQLiteOpenHelper openHelper(ProductionOpenHelper openHelper);

    @Provides
    @Singleton
    public static DaoMaster providesDaoMaster(@NonNull SQLiteOpenHelper openHelper) {
        return new DaoMaster(openHelper.getWritableDatabase());
    }

    @Provides
    @Singleton
    public static DaoSession provideDaoSession(@NonNull DaoMaster daoMaster) {
        return daoMaster.newSession();
    }
}
