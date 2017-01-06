package com.github.st1hy.countthemcalories.inject.activities.addmeal;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreenImpl;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AddMealActivityBindings {

    @Binds
    public abstract Activity activity(AppCompatActivity activity);

    @Binds
    @Named("activityContext")
    public abstract Context context(AppCompatActivity activity);

    @Binds
    public abstract AddMealScreen addMealScreen(AddMealScreenImpl screen);
}
