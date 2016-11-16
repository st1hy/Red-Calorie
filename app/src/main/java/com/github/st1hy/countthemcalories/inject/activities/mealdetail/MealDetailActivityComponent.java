package com.github.st1hy.countthemcalories.inject.activities.mealdetail;

import com.github.st1hy.countthemcalories.inject.activities.mealdetail.fragment.MealDetailComponentFactory;
import com.github.st1hy.countthemcalories.activities.mealdetail.MealDetailActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = MealDetailActivityModel.class)
public interface MealDetailActivityComponent extends MealDetailComponentFactory {

    void inject(MealDetailActivity activity);

}
