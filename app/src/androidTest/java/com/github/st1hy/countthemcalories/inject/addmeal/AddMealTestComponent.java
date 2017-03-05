package com.github.st1hy.countthemcalories.inject.addmeal;

import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityModule;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import dagger.Subcomponent;


@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        AddMealActivityModule.class,
        AddMealTestModule.class,
})
public interface AddMealTestComponent extends AddMealActivityComponent {

    @Override
    AddMealTestFragmentComponent newAddMealFragmentComponent(FragmentModule module);
}
