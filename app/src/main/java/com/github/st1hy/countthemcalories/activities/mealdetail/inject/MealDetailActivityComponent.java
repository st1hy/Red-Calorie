package com.github.st1hy.countthemcalories.activities.mealdetail.inject;

import com.github.st1hy.countthemcalories.activities.mealdetail.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject.MealDetailComponentFactory;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        MealDetailActivityModel.class
})
public interface MealDetailActivityComponent extends MealDetailComponentFactory {

    void inject(MealDetailActivity activity);

}
