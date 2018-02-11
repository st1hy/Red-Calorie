package com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.inject;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.AddMealFragment;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.adapter.inject.IngredientListComponentFactory;
import com.github.st1hy.countthemcalories.ui.inject.core.PictureModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

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
