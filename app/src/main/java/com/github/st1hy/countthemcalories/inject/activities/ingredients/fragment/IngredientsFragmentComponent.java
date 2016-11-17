package com.github.st1hy.countthemcalories.inject.activities.ingredients.fragment;

import com.github.st1hy.countthemcalories.activities.ingredients.fragment.IngredientsFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.core.DialogModule;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        IngredientsFragmentModule.class,
        PermissionModule.class,
        DialogModule.class
})
public interface IngredientsFragmentComponent {

    void inject(IngredientsFragment fragment);
}
