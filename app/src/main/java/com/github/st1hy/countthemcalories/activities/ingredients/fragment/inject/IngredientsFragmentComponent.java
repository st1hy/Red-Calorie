package com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject;

import com.github.st1hy.countthemcalories.activities.ingredients.fragment.IngredientsFragment;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.inject.IngredientViewHolderComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.inject.IngredientViewHolderModule;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        IngredientsFragmentModule.class,
})
public interface IngredientsFragmentComponent {

    void inject(IngredientsFragment fragment);

    IngredientViewHolderComponent newIngredientViewHolderComponent(IngredientViewHolderModule module);
}
