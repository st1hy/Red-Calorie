package com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.inject.IngredientListComponentFactory;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;
import com.github.st1hy.countthemcalories.inject.core.headerpicture.PictureModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        AddMealFragmentModule.class,
        PictureModule.class,
})
public interface AddMealFragmentComponent extends IngredientListComponentFactory {

    void inject(AddMealFragment fragment);

}
