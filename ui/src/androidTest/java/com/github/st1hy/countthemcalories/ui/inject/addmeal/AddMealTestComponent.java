package com.github.st1hy.countthemcalories.ui.inject.addmeal;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.inject.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.inject.AddMealActivityModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

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
