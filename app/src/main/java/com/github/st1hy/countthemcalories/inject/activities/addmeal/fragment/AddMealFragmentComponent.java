package com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.AddMealFragment;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.ingredientitems.IngredientListComponentFactory;
import com.github.st1hy.countthemcalories.inject.core.DialogModule;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.inject.core.headerpicture.PictureModule;
import com.github.st1hy.countthemcalories.inject.core.RecyclerViewAdapterDelegateModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        AddMealFragmentModule.class,
        PictureModule.class,
        PermissionModule.class,
        DialogModule.class,
        RecyclerViewAdapterDelegateModule.class
})
public interface AddMealFragmentComponent extends IngredientListComponentFactory {

    void inject(AddMealFragment fragment);

}
