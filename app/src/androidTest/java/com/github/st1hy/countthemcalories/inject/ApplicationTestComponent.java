package com.github.st1hy.countthemcalories.inject;

import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.application.inject.ApplicationModule;
import com.github.st1hy.countthemcalories.application.inject.SettingsModule;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class, SettingsModule.class})
public interface ApplicationTestComponent extends ApplicationComponent {
}
