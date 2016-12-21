package com.github.st1hy.countthemcalories.inject.application;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.AddIngredientActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.AddMealActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.IngredientDetailActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.IngredientsActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.mealdetail.MealDetailActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.overview.OverviewActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.settings.SettingsActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.tags.TagsActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.core.activityresult.IntentHandlerActivityComponentFactory;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class, SettingsModule.class, DbModelsModule.class})
public interface ApplicationComponent extends AddMealActivityComponentFactory,
        MealDetailActivityComponentFactory,
        IngredientDetailActivityComponentFactory,
        SettingsActivityComponentFactory,
        OverviewActivityComponentFactory,
        IntentHandlerActivityComponentFactory,
        AddIngredientActivityComponentFactory,
        IngredientsActivityComponentFactory,
        TagsActivityComponentFactory {

    void inject(CaloriesCounterApplication application);

}
