package com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealFragment;
import com.github.st1hy.countthemcalories.core.dialog.DialogModule;
import com.github.st1hy.countthemcalories.core.headerpicture.inject.PictureModule;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {AddMealFragmentModule.class, PictureModule.class, PermissionModule.class, DialogModule.class})
public interface AddMealFragmentComponent extends IngredientListComponentFactory {

    void inject(AddMealFragment fragment);

}
