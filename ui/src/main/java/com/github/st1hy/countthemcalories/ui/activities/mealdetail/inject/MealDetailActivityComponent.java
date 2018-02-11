package com.github.st1hy.countthemcalories.ui.activities.mealdetail.inject;

import com.github.st1hy.countthemcalories.ui.activities.mealdetail.MealDetailActivity;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.inject.MealDetailComponentFactory;
import com.github.st1hy.countthemcalories.ui.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        MealDetailActivityModel.class,
        PermissionModule.class,
})
public interface MealDetailActivityComponent extends MealDetailComponentFactory {

    void inject(MealDetailActivity activity);

}
