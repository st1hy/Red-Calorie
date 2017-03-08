package com.github.st1hy.countthemcalories.application.inject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.github.st1hy.countthemcalories.inject.quantifier.context.AppContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class SettingsModule {

    @Provides
    @Singleton
    public static SharedPreferences providePreferences(@AppContext Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    public static Resources provideResources(@AppContext Context context) {
        return context.getResources();
    }
}
