package com.github.st1hy.countthemcalories.inject.addingredient;

import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.AddIngredientComponent;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.AddIngredientModule;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.fragment.AddIngredientFragmentModule;
import com.github.st1hy.countthemcalories.inject.core.ToolbarNavigateBackModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        AddIngredientModule.class,
        ToolbarNavigateBackModule.class,
        AddIngredientTestModule.class
})
public interface AddIngredientTestComponent extends AddIngredientComponent {

    @Override
    AddIngredientTestFragmentComponent newAddIngredientFragmentComponent(AddIngredientFragmentModule module);

}
