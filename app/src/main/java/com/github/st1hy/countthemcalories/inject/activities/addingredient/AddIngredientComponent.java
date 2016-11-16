package com.github.st1hy.countthemcalories.inject.activities.addingredient;


import com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity;
import com.github.st1hy.countthemcalories.inject.application.ApplicationComponent;
import com.github.st1hy.countthemcalories.inject.core.PictureModule;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = {AddIngredientModule.class, PictureModule.class},
        dependencies = ApplicationComponent.class)
public interface AddIngredientComponent {

    void inject(AddIngredientActivity activity);


}
