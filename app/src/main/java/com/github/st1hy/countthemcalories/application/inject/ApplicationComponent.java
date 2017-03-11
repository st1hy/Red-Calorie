package com.github.st1hy.countthemcalories.application.inject;

import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientActivityComponentFactory;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponentFactory;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailActivityComponentFactory;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityComponentFactory;
import com.github.st1hy.countthemcalories.activities.mealdetail.inject.MealDetailActivityComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.inject.OverviewActivityComponentFactory;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.inject.DatabaseModule;
import com.github.st1hy.countthemcalories.activities.settings.inject.SettingsActivityComponentFactory;
import com.github.st1hy.countthemcalories.activities.tags.inject.TagsActivityComponentFactory;
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
