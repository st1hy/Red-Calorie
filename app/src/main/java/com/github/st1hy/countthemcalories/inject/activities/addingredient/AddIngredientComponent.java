package com.github.st1hy.countthemcalories.inject.activities.addingredient;


import com.github.st1hy.countthemcalories.activities.addingredient.AddIngredientActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.fragment.AddIngredientFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.core.ToolbarNavigateBackModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        AddIngredientModule.class,
        ToolbarNavigateBackModule.class
})
public interface AddIngredientComponent extends AddIngredientFragmentComponentFactory {

    void inject(AddIngredientActivity activity);

}
