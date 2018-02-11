package com.github.st1hy.countthemcalories.ui.inject.addingredient;

import com.github.st1hy.countthemcalories.ui.activities.addingredient.inject.AddIngredientComponent;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.inject.AddIngredientModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

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
