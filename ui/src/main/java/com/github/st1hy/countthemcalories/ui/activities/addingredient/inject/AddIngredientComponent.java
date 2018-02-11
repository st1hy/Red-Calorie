package com.github.st1hy.countthemcalories.ui.activities.addingredient.inject;


import com.github.st1hy.countthemcalories.ui.activities.addingredient.AddIngredientActivity;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.inject.AddIngredientFragmentComponentFactory;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        AddIngredientModule.class,
})
public interface AddIngredientComponent extends AddIngredientFragmentComponentFactory {

    void inject(AddIngredientActivity activity);

}
