package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.fragment.inject;

import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.fragment.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        IngredientsDetailFragmentModule.class,
})
public interface IngredientDetailFragmentComponent {

    void inject(IngredientDetailFragment fragment);
}
