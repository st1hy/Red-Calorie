package com.github.st1hy.countthemcalories.inject.activities.addingredient.fragment;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {AddIngredientFragmentModule.class, PermissionModule.class})
public interface AddIngredientFragmentComponent {

    void inject(AddIngredientFragment fragment);
}
