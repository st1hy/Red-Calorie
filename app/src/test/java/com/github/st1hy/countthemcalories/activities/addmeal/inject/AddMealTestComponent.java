package com.github.st1hy.countthemcalories.activities.addmeal.inject;

import com.github.st1hy.countthemcalories.core.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.database.DaoSession;

import dagger.Component;

@PerActivity
@Component(modules = AddMealActivityModule.class, dependencies = ApplicationTestComponent.class)
public interface AddMealTestComponent extends AddMealActivityComponent {

    DaoSession getDaoSession();

}
