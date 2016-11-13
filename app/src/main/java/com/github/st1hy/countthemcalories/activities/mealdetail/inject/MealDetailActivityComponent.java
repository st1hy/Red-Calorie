package com.github.st1hy.countthemcalories.activities.mealdetail.inject;

import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject.MealDetailComponent;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject.MealDetailsModule;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = MealDetailActivityModel.class, dependencies = ApplicationComponent.class)
public interface MealDetailActivityComponent {

    void inject(MealDetailActivity activity);

    MealDetailComponent mealDetailComponent(MealDetailsModule mealDetailsModule);

}
