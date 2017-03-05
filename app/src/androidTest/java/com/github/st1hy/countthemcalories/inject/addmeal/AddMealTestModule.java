package com.github.st1hy.countthemcalories.inject.addmeal;

import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponent;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AddMealTestModule {

    @Binds
    public abstract AddMealActivityComponent addMealTestComponent(AddMealTestComponent component);
}
