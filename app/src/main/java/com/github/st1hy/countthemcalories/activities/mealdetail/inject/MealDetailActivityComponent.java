package com.github.st1hy.countthemcalories.activities.mealdetail.inject;

import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject.MealDetailComponentFactory;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = MealDetailActivityModel.class, dependencies = ApplicationComponent.class)
public interface MealDetailActivityComponent extends MealDetailComponentFactory {

    void inject(MealDetailActivity activity);

}
