package com.github.st1hy.countthemcalories.inject.addmeal;

import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.AddMealFragmentComponent;

import dagger.Module;
import dagger.Provides;

@Module
public class AddMealTestFragmentModule {

    @Provides
    public AddMealFragmentComponent addMealFragmentComponent(AddMealTestFragmentComponent component) {
        return component;
    }
}
