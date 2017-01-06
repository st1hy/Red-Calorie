package com.github.st1hy.countthemcalories.database.inject;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.DaoMaster;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.production.DatabaseMigrationHelper;
import com.github.st1hy.countthemcalories.database.production.ProductionOpenHelper;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public DatabaseMigrationHelper provideDatabaseMigrationHelper() {
        return new DatabaseMigrationHelper();
    }

    @Provides
    @Singleton
    public DaoMaster.OpenHelper provideDbOpenHelper(@NonNull @Named("appContext") Context context,
                                                    @NonNull DatabaseMigrationHelper migrationHelper) {
        return new ProductionOpenHelper(context, "database.db", null, migrationHelper);
    }

    @Provides
    @Singleton
    public DaoMaster providesDaoMaster(@NonNull DaoMaster.OpenHelper openHelper) {
        return new DaoMaster(openHelper.getWritableDatabase());
    }

    @Provides
    @Singleton
    public DaoSession provideDaoSession(@NonNull DaoMaster daoMaster) {
        return daoMaster.newSession();
    }
}
