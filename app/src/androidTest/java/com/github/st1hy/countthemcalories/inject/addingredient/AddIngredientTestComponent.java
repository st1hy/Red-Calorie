package com.github.st1hy.countthemcalories.inject.addingredient;

import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientModule;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        AddIngredientModule.class,
        AddIngredientTestModule.class,
})
public interface AddIngredientTestComponent extends AddIngredientComponent {

    @Override
    AddIngredientTestFragmentComponent newAddIngredientFragmentComponent(FragmentModule module);

}
