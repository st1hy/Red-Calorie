package com.github.st1hy.countthemcalories.activities.addingredient.inject;


import com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        AddIngredientModule.class,
})
public interface AddIngredientComponent extends AddIngredientFragmentComponentFactory {

    void inject(AddIngredientActivity activity);

}
