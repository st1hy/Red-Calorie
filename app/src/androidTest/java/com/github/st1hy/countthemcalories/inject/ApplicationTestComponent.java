package com.github.st1hy.countthemcalories.inject;

import com.github.st1hy.countthemcalories.inject.application.ApplicationComponent;
import com.github.st1hy.countthemcalories.inject.application.ApplicationModule;
import com.github.st1hy.countthemcalories.inject.application.DbModelsModule;
import com.github.st1hy.countthemcalories.inject.application.SettingsModule;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class, SettingsModule.class, DbModelsModule.class})
public interface ApplicationTestComponent extends ApplicationComponent {

    DaoSession getDaoSession();
}
