package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        IngredientsDetailFragmentModule.class,
})
public interface IngredientDetailFragmentComponent {

    void inject(IngredientDetailFragment fragment);
}
