package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {IngredientsDetailFragmentModule.class, PermissionModule.class})
public interface IngredientDetailFragmentComponent {

    void inject(IngredientDetailFragment fragment);
}
