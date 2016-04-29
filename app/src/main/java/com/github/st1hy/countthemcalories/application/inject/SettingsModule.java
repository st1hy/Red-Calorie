package com.github.st1hy.countthemcalories.application.inject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsModule {

    @Provides
    @Singleton
    public SharedPreferences providePreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    public Resources provideResources(Context context) {
        return context.getResources();
    }

    @Provides
    @Singleton
    public SettingsModel provideSettingsModel(SharedPreferences preferences, Resources resources) {
        return new SettingsModel(preferences, resources);
    }
}