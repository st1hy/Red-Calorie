package com.github.st1hy.countthemcalories.inject.application;

import android.content.Context;

import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.core.activityresult.RxActivityResult;
import com.github.st1hy.countthemcalories.core.permissions.PersistentPermissionCache;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.AddIngredientActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.AddMealActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.contribute.ContributeComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.IngredientDetailActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.mealdetail.MealDetailActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.overview.OverviewActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.settings.SettingsActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.core.activityresult.IntentHandlerActivityComponentFactory;
import com.squareup.picasso.Picasso;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class, SettingsModule.class, DbModelsModule.class})
public interface ApplicationComponent extends AddMealActivityComponentFactory,
        MealDetailActivityComponentFactory,
        IngredientDetailActivityComponentFactory,
        SettingsActivityComponentFactory,
        ContributeComponentFactory,
        OverviewActivityComponentFactory,
        IntentHandlerActivityComponentFactory,
        AddIngredientActivityComponentFactory {

    @Named("appContext")
    Context getContext();

    Picasso getPicasso();

    RxTagsDatabaseModel getTagsModel();

    RxIngredientsDatabaseModel getIngredientTypesModel();

    RxMealsDatabaseModel getMealsModel();

    PersistentPermissionCache getPermissionCache();

    RxActivityResult getRxActivityResult();

    void inject(CaloriesCounterApplication application);

}
