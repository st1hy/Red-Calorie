package com.github.st1hy.countthemcalories.activities.ingredients.inject;

import com.github.st1hy.countthemcalories.core.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.database.DaoSession;

import dagger.Component;

@PerActivity
@Component(modules = IngredientsActivityModule.class, dependencies = ApplicationTestComponent.class)
public interface IngredientsTestComponent extends IngredientsActivityComponent {

    DaoSession getDaoSession();
}
