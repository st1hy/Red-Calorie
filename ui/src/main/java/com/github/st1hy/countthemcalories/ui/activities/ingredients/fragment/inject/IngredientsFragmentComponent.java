package com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.inject;

import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.IngredientsFragment;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.inject.IngredientViewHolderComponent;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.inject.IngredientViewHolderModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

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
