package com.github.st1hy.countthemcalories.application.inject;

import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientActivityComponentFactory;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponentFactory;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.inject.DatabaseModule;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.IngredientsActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.mealdetail.MealDetailActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.overview.OverviewActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.settings.SettingsActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.tags.TagsActivityComponentFactory;
import com.github.st1hy.countthemcalories.inject.common.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        SettingsModule.class,
        DatabaseModule.class,
})
public interface ApplicationComponent extends AddMealActivityComponentFactory,
        MealDetailActivityComponentFactory,
        IngredientDetailActivityComponentFactory,
        SettingsActivityComponentFactory,
        OverviewActivityComponentFactory,
        AddIngredientActivityComponentFactory,
        IngredientsActivityComponentFactory,
        TagsActivityComponentFactory {

    void inject(CaloriesCounterApplication application);

}
