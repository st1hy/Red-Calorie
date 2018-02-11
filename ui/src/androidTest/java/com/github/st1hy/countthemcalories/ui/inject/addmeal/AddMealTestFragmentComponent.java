package com.github.st1hy.countthemcalories.ui.inject.addmeal;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.inject.AddMealFragmentComponent;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.inject.AddMealFragmentModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;
import com.github.st1hy.countthemcalories.ui.inject.core.TestPictureModule;

import dagger.Subcomponent;


@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        AddMealFragmentModule.class,
        TestPictureModule.class,
        AddMealTestFragmentModule.class
})
public interface AddMealTestFragmentComponent extends AddMealFragmentComponent {
}
