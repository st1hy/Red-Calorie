package com.github.st1hy.countthemcalories.activities.ingredients.inject;

import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

public interface IngredientsActivityComponentFactory {

    IngredientsActivityComponent newIngredientsActivityComponent(ActivityModule module);
}
