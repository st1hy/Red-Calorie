package com.github.st1hy.countthemcalories.activities.addingredient.inject;


import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = AddIngredientModule.class, dependencies = ApplicationComponent.class)
public interface AddIngredientComponent {

    void inject(AddIngredientActivity activity);

}
