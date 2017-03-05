package com.github.st1hy.countthemcalories.inject.addmeal;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentModule;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;
import com.github.st1hy.countthemcalories.inject.core.TestPictureModule;

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
