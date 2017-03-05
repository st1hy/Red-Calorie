package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

public interface AddIngredientActivityComponentFactory {

    AddIngredientComponent newAddIngredientActivityComponent(ActivityModule activityModule);
}
