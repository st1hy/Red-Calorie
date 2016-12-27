package com.github.st1hy.countthemcalories.inject.addingredient;


import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.fragment.AddIngredientFragmentComponent;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.fragment.AddIngredientFragmentModule;
import com.github.st1hy.countthemcalories.inject.core.DialogModule;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.inject.core.RecyclerViewAdapterDelegateModule;
import com.github.st1hy.countthemcalories.inject.core.TestPictureModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        AddIngredientFragmentModule.class,
        PermissionModule.class,
        DialogModule.class,
        TestPictureModule.class,
        RecyclerViewAdapterDelegateModule.class
})
public interface AddIngredientTestFragmentComponent extends AddIngredientFragmentComponent {

}
