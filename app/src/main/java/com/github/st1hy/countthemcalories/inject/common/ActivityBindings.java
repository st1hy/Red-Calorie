package com.github.st1hy.countthemcalories.inject.common;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.inject.quantifier.context.ActivityContext;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ActivityBindings {

    @Binds
    public abstract Activity activity(AppCompatActivity activity);

    @Binds
    @ActivityContext
    public abstract Context activityContext(AppCompatActivity activity);
}
