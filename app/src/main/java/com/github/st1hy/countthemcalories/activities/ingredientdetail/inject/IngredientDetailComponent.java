package com.github.st1hy.countthemcalories.activities.ingredientdetail.inject;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientDetailFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        IngredientDetailModule.class,
})
public interface IngredientDetailComponent extends IngredientDetailFragmentComponentFactory {

    void inject(IngredientDetailActivity activity);
}
