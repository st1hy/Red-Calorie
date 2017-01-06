package com.github.st1hy.countthemcalories.inject.activities.ingredientdetail;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreenImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class IngredientDetailBindings {

    @Binds
    public abstract IngredientDetailScreen ingredientDetailScreen(IngredientDetailScreenImpl screen);

    @Binds
    public abstract Activity activity(AppCompatActivity activity);
}
