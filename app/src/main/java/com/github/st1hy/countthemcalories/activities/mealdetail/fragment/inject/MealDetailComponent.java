package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject;

import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailFragment;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;

import dagger.Component;

@PerFragment
@Component(modules = {MealDetailsModule.class, PermissionModule.class},
        dependencies = ApplicationComponent.class)
public interface MealDetailComponent {

    void inject(MealDetailFragment fragment);
}
