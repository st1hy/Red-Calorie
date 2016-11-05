package com.github.st1hy.countthemcalories.activities.settings.inject;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenterImpl;
import com.github.st1hy.countthemcalories.core.drawer.DrawerView;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsActivityModule {

    private final SettingsActivity activity;

    public SettingsActivityModule(@NonNull SettingsActivity activity) {
        this.activity = activity;
    }

    @PerActivity
    @Provides
    public DrawerPresenter provideDrawerPresenter(DrawerView drawerView) {
        return new DrawerPresenterImpl(drawerView, DrawerMenuItem.SETTINGS);
    }

    @Provides
    public DrawerView provideDrawerView() {
        return activity;
    }
}
