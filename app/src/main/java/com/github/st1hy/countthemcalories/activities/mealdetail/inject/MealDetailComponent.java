package com.github.st1hy.countthemcalories.activities.mealdetail.inject;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = MealDetailsModule.class, dependencies = ApplicationComponent.class)
public interface MealDetailComponent {

    void inject(@NonNull MealDetailActivity activity);
}
