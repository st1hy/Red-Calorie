package com.github.st1hy.countthemcalories.inject;

import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.AddIngredientModule;
import com.github.st1hy.countthemcalories.inject.addingredient.AddIngredientTestComponent;
import com.github.st1hy.countthemcalories.inject.application.ApplicationComponent;
import com.github.st1hy.countthemcalories.inject.application.ApplicationModule;
import com.github.st1hy.countthemcalories.inject.application.DbModelsModule;
import com.github.st1hy.countthemcalories.inject.application.SettingsModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        DatabaseModule.class,
        SettingsModule.class,
        DbModelsModule.class,
        ActivityInjectModule.class
})
public interface ApplicationTestComponent extends ApplicationComponent {

    DaoSession getDaoSession();

    SettingsModel getSettingsModel();

    AddIngredientTestComponent newAddIngredientTestComponent(AddIngredientModule module);
}
