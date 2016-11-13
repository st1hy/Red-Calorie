package com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientFragment;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {AddIngredientFragmentModule.class, PermissionModule.class})
public interface AddIngredientFragmentComponent {

    void inject(AddIngredientFragment fragment);
}
