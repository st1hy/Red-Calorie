package com.github.st1hy.countthemcalories.inject.addmeal;

import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.AddMealActivityModule;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.AddMealFragmentModule;
import com.github.st1hy.countthemcalories.inject.core.ToolbarNavigateBackModule;

import dagger.Subcomponent;


@PerActivity
@Subcomponent(modules = {
        AddMealActivityModule.class,
        ToolbarNavigateBackModule.class,
        AddMealTestModule.class
})
public interface AddMealTestComponent extends AddMealActivityComponent {

    @Override
    AddMealTestFragmentComponent newAddMealFragmentComponent(AddMealFragmentModule addMealFragmentModule);
}
