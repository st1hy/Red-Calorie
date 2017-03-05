package com.github.st1hy.countthemcalories.inject.common;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import dagger.Module;
import dagger.Provides;

@Module(includes = ActivityBindings.class)
public class ActivityModule {

    @NonNull
    private final AppCompatActivity activity;

    public ActivityModule(@NonNull AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    public AppCompatActivity appCompatActivity() {
        return activity;
    }

    @Provides
    public Intent provideIntent(Activity activity) {
        return activity.getIntent();
    }

    @Provides
    public static FragmentManager provideFragmentManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }
}
