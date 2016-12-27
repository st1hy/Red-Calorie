package com.github.st1hy.countthemcalories.inject.addmeal;

import com.github.st1hy.countthemcalories.inject.activities.addmeal.AddMealActivityComponent;

import dagger.Module;
import dagger.Provides;

@Module
public class AddMealTestModule {

    @Provides
    public AddMealActivityComponent addMealTestComponent(AddMealTestComponent component) {
        return component;
    }
}
