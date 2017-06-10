package com.github.st1hy.countthemcalories.activities.ingredients.inject;

import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject.IngredientsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        IngredientsActivityModule.class,
})
public interface IngredientsActivityComponent extends IngredientsFragmentComponentFactory {

    void inject(IngredientsActivity activity);

}
