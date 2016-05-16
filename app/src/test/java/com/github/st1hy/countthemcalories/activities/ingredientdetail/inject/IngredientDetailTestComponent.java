package com.github.st1hy.countthemcalories.activities.ingredientdetail.inject;

import com.github.st1hy.countthemcalories.core.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.database.DaoSession;

import dagger.Component;

@PerActivity
@Component(modules = IngredientDetailsModule.class, dependencies = ApplicationTestComponent.class)
public interface IngredientDetailTestComponent extends IngredientDetailComponent {

    DaoSession getDaoSession();

}
