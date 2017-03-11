package com.github.st1hy.countthemcalories.activities.settings.inject;

import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.settings.fragment.SettingsFragment;
import com.github.st1hy.countthemcalories.activities.settings.fragment.inject.SettingsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class SettingsActivityModule {

    @Binds
    public abstract SettingsFragmentComponentFactory fragmentComponentFactory(SettingsActivityComponent component);

    @Provides
    public static DrawerMenuItem drawerMenuItem() {
        return DrawerMenuItem.SETTINGS;
    }

    @Provides
    public static SettingsFragment settingsFragment(AppCompatActivity activity,
                                                    SettingsFragmentComponentFactory factory) {
        SettingsFragment fragment = (SettingsFragment) activity.getSupportFragmentManager()
                .findFragmentByTag("settings_content_fragment");
        fragment.setComponentFactory(factory);
        return fragment;
    }

}
