package com.github.st1hy.countthemcalories.activities.mealdetail.inject;

import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject.MealDetailComponentFactory;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = MealDetailActivityModel.class)
public interface MealDetailActivityComponent extends MealDetailComponentFactory {

    void inject(MealDetailActivity activity);

}
