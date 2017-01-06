package com.github.st1hy.countthemcalories.inject.activities.mealdetail;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailScreen;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailScreenImpl;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MealDetailActivityBindings {

    @Binds
    public abstract Activity activity(AppCompatActivity activity);

    @Binds
    public abstract MealDetailScreen mealDetailScreen(MealDetailScreenImpl screen);

    @Binds
    @Named("activityContext")
    public abstract Context context(Activity activity);
}
