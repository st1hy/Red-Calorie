package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject;

import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailFragment;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {MealDetailsModule.class, PermissionModule.class})
public interface MealDetailComponent {

    void inject(MealDetailFragment fragment);
}
