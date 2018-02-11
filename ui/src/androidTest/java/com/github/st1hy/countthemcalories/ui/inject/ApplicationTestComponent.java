package com.github.st1hy.countthemcalories.ui.inject;

import com.github.st1hy.countthemcalories.ui.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.HeaderPicturePickerUtils;
import com.github.st1hy.countthemcalories.ui.inject.addingredient.AddIngredientTestComponent;
import com.github.st1hy.countthemcalories.ui.inject.addmeal.AddMealTestComponent;
import com.github.st1hy.countthemcalories.ui.inject.app.ApplicationComponent;
import com.github.st1hy.countthemcalories.ui.inject.app.SettingsModule;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.inject.DatabaseModule;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;
import com.github.st1hy.countthemcalories.ui.inject.app.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        SettingsModule.class,
        DatabaseModule.class,
})
public interface ApplicationTestComponent extends ApplicationComponent {

    DaoSession getDaoSession();

    SettingsModel getSettingsModel();

    @Override
    AddIngredientTestComponent newAddIngredientActivityComponent(ActivityModule activityModule);

    @Override
    AddMealTestComponent newAddMealActivityComponent(ActivityModule activityModule);

    HeaderPicturePickerUtils testHeaderPicturePickerUtils();
}
