package com.github.st1hy.countthemcalories.activities.ingredientdetail.inject;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientDetailFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = IngredientDetailModule.class)
public interface IngredientDetailComponent extends IngredientDetailFragmentComponentFactory {

    void inject(IngredientDetailActivity activity);
}
