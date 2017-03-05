package com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        AddIngredientFragmentModule.class,
})
public interface AddIngredientFragmentComponent {

    void inject(AddIngredientFragment fragment);
}
