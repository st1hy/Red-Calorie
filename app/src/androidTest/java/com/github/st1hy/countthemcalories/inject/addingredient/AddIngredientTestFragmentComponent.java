package com.github.st1hy.countthemcalories.inject.addingredient;


import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentModule;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;
import com.github.st1hy.countthemcalories.inject.core.DialogModule;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.inject.core.TestPictureModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        AddIngredientFragmentModule.class,
        PermissionModule.class,
        DialogModule.class,
        TestPictureModule.class,
        FragmentModule.class
})
public interface AddIngredientTestFragmentComponent extends AddIngredientFragmentComponent {

}
