package com.github.st1hy.countthemcalories.activities.ingredientdetail.inject;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = IngredientDetailModule.class, dependencies = ApplicationComponent.class)
public interface IngredientDetailComponent {

    void inject(@NonNull IngredientDetailActivity activity);
}
