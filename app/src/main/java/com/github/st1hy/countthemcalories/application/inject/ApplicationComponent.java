package com.github.st1hy.countthemcalories.application.inject;

import android.app.Application;
import android.content.Context;

import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDatabaseModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.activities.tags.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class, SettingsModule.class, DbModelsModule.class})
public interface ApplicationComponent {

    @Singleton
    Context getContext();

    @Singleton
    Application getApplication();

    Picasso getPicasso();

    SettingsModel getSettingsModel();

    RxTagsDatabaseModel getTagsModel();

    IngredientTypesDatabaseModel getIngredientTypesModel();

    MealDatabaseModel getMealsModel();

    void inject(CaloriesCounterApplication application);

}
