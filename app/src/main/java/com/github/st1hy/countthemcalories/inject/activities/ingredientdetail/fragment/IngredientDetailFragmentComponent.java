package com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        IngredientsDetailFragmentModule.class,
        PermissionModule.class
})
public interface IngredientDetailFragmentComponent {

    void inject(IngredientDetailFragment fragment);
}
