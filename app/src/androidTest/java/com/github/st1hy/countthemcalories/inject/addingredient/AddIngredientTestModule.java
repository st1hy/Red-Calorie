package com.github.st1hy.countthemcalories.inject.addingredient;

import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientComponent;

import dagger.Module;
import dagger.Provides;

@Module
public class AddIngredientTestModule {

    @Provides
    public AddIngredientComponent component(AddIngredientTestComponent component) {
        return component;
    }

}
