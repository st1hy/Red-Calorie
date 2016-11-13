package com.github.st1hy.countthemcalories.activities.settings.inject;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsActivityModule {

    private final SettingsActivity activity;

    public SettingsActivityModule(@NonNull SettingsActivity activity) {
        this.activity = activity;
    }

    @Provides
    public AppCompatActivity appCompatActivity() {
        return activity;
    }

    @Provides
    public Activity activity() {
        return activity;
    }

    @Provides
    public DrawerMenuItem drawerMenuItem() {
        return DrawerMenuItem.SETTINGS;
    }
}
