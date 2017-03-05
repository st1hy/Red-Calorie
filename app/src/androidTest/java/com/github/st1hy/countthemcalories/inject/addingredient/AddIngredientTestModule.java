package com.github.st1hy.countthemcalories.inject.addingredient;

import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientComponent;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AddIngredientTestModule {

    @Binds
    public abstract AddIngredientComponent component(AddIngredientTestComponent component);

}
