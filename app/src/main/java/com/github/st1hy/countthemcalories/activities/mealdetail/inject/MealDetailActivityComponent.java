package com.github.st1hy.countthemcalories.activities.mealdetail.inject;

import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = MealDetailActivityModel.class)
public interface MealDetailActivityComponent {

    void inject(MealDetailActivity activity);
}
