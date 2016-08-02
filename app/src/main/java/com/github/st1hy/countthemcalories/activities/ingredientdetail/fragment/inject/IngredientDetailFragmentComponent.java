package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;

import dagger.Component;

@PerFragment
@Component(modules = {IngredientsDetailFragmentModule.class, PermissionModule.class},
        dependencies = ApplicationComponent.class)
public interface IngredientDetailFragmentComponent {

    void inject(IngredientDetailFragment fragment);
}
