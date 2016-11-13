package com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject;

import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsFragment;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {IngredientsFragmentModule.class, PermissionModule.class})
public interface IngredientsFragmentComponent {

    void inject(IngredientsFragment fragment);
}
