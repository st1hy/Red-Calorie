package com.github.st1hy.countthemcalories.inject.addmeal;

import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.AddMealFragmentComponent;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.AddMealFragmentModule;
import com.github.st1hy.countthemcalories.inject.core.DialogModule;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.inject.core.RecyclerViewAdapterDelegateModule;
import com.github.st1hy.countthemcalories.inject.core.TestPictureModule;

import dagger.Subcomponent;


@PerFragment
@Subcomponent(modules = {
        AddMealFragmentModule.class,
        TestPictureModule.class,
        PermissionModule.class,
        DialogModule.class,
        RecyclerViewAdapterDelegateModule.class,
        AddMealTestFragmentModule.class
})
public interface AddMealTestFragmentComponent extends AddMealFragmentComponent {
}
