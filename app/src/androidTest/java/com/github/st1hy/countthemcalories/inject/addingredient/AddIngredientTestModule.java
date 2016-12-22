package com.github.st1hy.countthemcalories.inject.addingredient;

import com.github.st1hy.countthemcalories.inject.activities.addingredient.AddIngredientComponent;

import dagger.Module;
import dagger.Provides;

@Module
public class AddIngredientTestModule {

    @Provides
    public AddIngredientComponent component(AddIngredientTestComponent component) {
        return component;
    }
}
