package com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.inject;

import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.PictureModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        AddIngredientFragmentModule.class,
        PictureModule.class,
})
public interface AddIngredientFragmentComponent {

    void inject(AddIngredientFragment fragment);
}
