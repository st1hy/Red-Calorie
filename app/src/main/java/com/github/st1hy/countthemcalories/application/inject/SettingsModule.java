package com.github.st1hy.countthemcalories.application.inject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsModule {

    @Provides
    @Singleton
    public SharedPreferences providePreferences(@Named("appContext") Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    public Resources provideResources(@Named("appContext") Context context) {
        return context.getResources();
    }
}
