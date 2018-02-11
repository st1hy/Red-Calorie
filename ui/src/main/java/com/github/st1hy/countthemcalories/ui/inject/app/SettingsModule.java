package com.github.st1hy.countthemcalories.ui.inject.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.AppContext;

import java.text.DecimalFormat;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

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

    @Provides
    @Reusable
    @TwoPlaces
    static DecimalFormat default2DecimalPlacesFormat() {
        return new DecimalFormat("0.##");
    }
}
