package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.inject;

import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.fragment.inject.IngredientDetailFragmentComponentFactory;
import com.github.st1hy.countthemcalories.ui.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        IngredientDetailModule.class,
        PermissionModule.class,
})
public interface IngredientDetailComponent extends IngredientDetailFragmentComponentFactory {

    void inject(IngredientDetailActivity activity);
}
