package com.github.st1hy.countthemcalories.activities.ingredientdetail.inject;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = IngredientDetailsModule.class, dependencies = ApplicationComponent.class)
public interface IngredientDetailComponent {

    void inject(@NonNull IngredientDetailsActivity activity);
}
