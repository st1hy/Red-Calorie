package com.github.st1hy.countthemcalories.ui.inject.addingredient;

import com.github.st1hy.countthemcalories.ui.activities.addingredient.inject.AddIngredientComponent;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AddIngredientTestModule {

    @Binds
    public abstract AddIngredientComponent component(AddIngredientTestComponent component);

}
