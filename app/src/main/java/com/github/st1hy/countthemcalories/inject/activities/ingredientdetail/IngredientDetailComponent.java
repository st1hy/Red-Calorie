package com.github.st1hy.countthemcalories.inject.activities.ingredientdetail;

import com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment.IngredientDetailFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = IngredientDetailModule.class)
public interface IngredientDetailComponent extends IngredientDetailFragmentComponentFactory {

    void inject(IngredientDetailActivity activity);
}
