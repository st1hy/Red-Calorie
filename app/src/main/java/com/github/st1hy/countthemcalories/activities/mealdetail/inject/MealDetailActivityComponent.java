package com.github.st1hy.countthemcalories.activities.mealdetail.inject;

import android.content.Context;

import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailScreen;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import javax.inject.Named;

import dagger.Component;

@PerActivity
@Component(modules = MealDetailActivityModel.class, dependencies = ApplicationComponent.class)
public interface MealDetailActivityComponent {

    void inject(MealDetailActivity activity);

    MealDetailScreen getMealDetailScreen();

    @Named("activityContext")
    Context getContext();

}
