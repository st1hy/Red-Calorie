package com.github.st1hy.countthemcalories.inject.activities.ingredientdetail;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment.IngredientDetailFragmentComponentFactory;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = IngredientDetailModule.class)
public interface IngredientDetailComponent extends IngredientDetailFragmentComponentFactory {

    void inject(IngredientDetailActivity activity);
}
