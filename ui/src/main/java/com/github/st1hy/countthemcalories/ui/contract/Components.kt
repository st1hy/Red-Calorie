package com.github.st1hy.countthemcalories.ui.contract

import com.github.st1hy.countthemcalories.ui.activities.addingredient.inject.AddIngredientActivityComponentFactory
import com.github.st1hy.countthemcalories.ui.activities.addmeal.inject.AddMealActivityComponentFactory
import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.inject.IngredientDetailActivityComponentFactory
import com.github.st1hy.countthemcalories.ui.activities.ingredients.inject.IngredientsActivityComponentFactory
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.inject.MealDetailActivityComponentFactory
import com.github.st1hy.countthemcalories.ui.activities.overview.inject.OverviewActivityComponentFactory
import com.github.st1hy.countthemcalories.ui.inject.settings.SettingsActivityComponentFactory
import com.github.st1hy.countthemcalories.ui.inject.tags.TagsActivityComponentFactory

interface AppComponent :
        AddMealActivityComponentFactory,
        MealDetailActivityComponentFactory,
        IngredientDetailActivityComponentFactory,
        SettingsActivityComponentFactory,
        OverviewActivityComponentFactory,
        AddIngredientActivityComponentFactory,
        IngredientsActivityComponentFactory,
        TagsActivityComponentFactory

/**
 * Interface for providing app component across application.
 * It needs to be implemented by current Application.
 */
interface AppComponentProvider {

    var component: AppComponent
}