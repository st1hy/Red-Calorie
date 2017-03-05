package com.github.st1hy.countthemcalories.inject.addingredient;


import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentModule;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        AddIngredientFragmentModule.class,
})
public interface AddIngredientTestFragmentComponent extends AddIngredientFragmentComponent {

}
