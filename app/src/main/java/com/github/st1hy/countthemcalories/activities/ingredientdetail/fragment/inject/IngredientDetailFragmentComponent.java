package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import dagger.Component;

@PerFragment
@Component(modules = IngredientsDetailFragmentModule.class, dependencies = ApplicationComponent.class)
public interface IngredientDetailFragmentComponent {

    void inject(IngredientDetailFragment fragment);
}
