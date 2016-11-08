package com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientFragment;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientComponent;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;

import dagger.Component;

@PerFragment
@Component(modules = {AddIngredientFragmentModule.class, PermissionModule.class},
        dependencies = {AddIngredientComponent.class})
public interface AddIngredientFragmentComponent {

    void inject(AddIngredientFragment fragment);
}
