package com.github.st1hy.countthemcalories.ui.inject.addingredient;


import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.inject.AddIngredientFragmentComponent;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.inject.AddIngredientFragmentModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;
import com.github.st1hy.countthemcalories.ui.inject.core.TestPictureModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        AddIngredientFragmentModule.class,
        TestPictureModule.class,
})
public interface AddIngredientTestFragmentComponent extends AddIngredientFragmentComponent {

}
