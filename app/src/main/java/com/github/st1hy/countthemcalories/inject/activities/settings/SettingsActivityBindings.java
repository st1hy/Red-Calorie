package com.github.st1hy.countthemcalories.inject.activities.settings;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class SettingsActivityBindings {

    @Binds
    @Named("activityContext")
    public abstract Context activityContext(AppCompatActivity activity);
}
