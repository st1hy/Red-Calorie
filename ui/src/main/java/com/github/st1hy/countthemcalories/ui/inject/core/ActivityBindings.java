package com.github.st1hy.countthemcalories.ui.inject.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class ActivityBindings {

    @Binds
    public abstract Activity activity(AppCompatActivity activity);

    @Binds
    @ActivityContext
    public abstract Context activityContext(AppCompatActivity activity);

    @Provides
    public static Intent provideIntent(Activity activity) {
        return activity.getIntent();
    }

    @Provides
    public static FragmentManager provideFragmentManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }

}
