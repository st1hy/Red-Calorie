package com.github.st1hy.countthemcalories.ui.inject.addmeal;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.inject.AddMealFragmentComponent;

import dagger.Module;
import dagger.Provides;

@Module
public class AddMealTestFragmentModule {

    @Provides
    public AddMealFragmentComponent addMealFragmentComponent(AddMealTestFragmentComponent component) {
        return component;
    }
}
