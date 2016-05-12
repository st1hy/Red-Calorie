package com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.inject;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view.IngredientDetailsActivity;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = IngredientDetailsModule.class, dependencies = ApplicationComponent.class)
public interface IngredientDetailComponent {

    void inject(@NonNull IngredientDetailsActivity activity);
}
