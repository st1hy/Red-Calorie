package com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;

import dagger.Component;

@PerFragment
@Component(modules = {AddMealFragmentModule.class, PermissionModule.class},
        dependencies = AddMealActivityComponent.class)
public interface AddMealFragmentComponent {

    void inject(AddMealFragment fragment);
}
