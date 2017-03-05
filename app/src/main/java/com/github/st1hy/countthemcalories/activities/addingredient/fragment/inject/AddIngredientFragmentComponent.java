package com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;
import com.github.st1hy.countthemcalories.inject.core.DialogModule;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.inject.core.headerpicture.PictureModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        AddIngredientFragmentModule.class,
        PermissionModule.class,
        DialogModule.class,
        PictureModule.class,
        FragmentModule.class
})
public interface AddIngredientFragmentComponent {

    void inject(AddIngredientFragment fragment);
}
