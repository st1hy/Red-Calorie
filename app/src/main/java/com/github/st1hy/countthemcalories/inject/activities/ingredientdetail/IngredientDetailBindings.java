package com.github.st1hy.countthemcalories.inject.activities.ingredientdetail;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreenImpl;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class IngredientDetailBindings {

    @Binds
    public abstract IngredientDetailScreen ingredientDetailScreen(IngredientDetailScreenImpl screen);

    @Binds
    public abstract Activity activity(AppCompatActivity activity);

    @Named("activityContext")
    @Binds abstract Context activityContext(AppCompatActivity activity);
}
