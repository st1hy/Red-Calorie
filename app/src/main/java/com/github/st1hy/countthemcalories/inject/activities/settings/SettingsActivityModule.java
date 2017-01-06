package com.github.st1hy.countthemcalories.inject.activities.settings;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.settings.SettingsActivity;
import com.github.st1hy.countthemcalories.activities.settings.fragment.SettingsFragment;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;

import dagger.Module;
import dagger.Provides;

@Module(includes = SettingsActivityBindings.class)
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
    public static DrawerMenuItem drawerMenuItem() {
        return DrawerMenuItem.SETTINGS;
    }

    @Provides
    public static SettingsFragment settingsFragment(AppCompatActivity activity,
                                                    SettingsActivityComponent component) {
        SettingsFragment fragment = (SettingsFragment) activity.getSupportFragmentManager()
                .findFragmentByTag("settings_content_fragment");
        fragment.setComponentFactory(component);
        return fragment;
    }

}
