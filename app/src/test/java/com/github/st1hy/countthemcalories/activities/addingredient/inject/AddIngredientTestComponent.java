package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import com.github.st1hy.countthemcalories.core.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.database.DaoSession;

import dagger.Component;

@PerActivity
@Component(modules = AddIngredientModule.class, dependencies = ApplicationTestComponent.class)
public interface AddIngredientTestComponent extends AddIngredientComponent {

    DaoSession getDaoSession();

}
