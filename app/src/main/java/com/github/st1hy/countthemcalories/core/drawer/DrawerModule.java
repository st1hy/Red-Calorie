package com.github.st1hy.countthemcalories.core.drawer;

import android.app.Activity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class DrawerModule {

    @Provides
    @PerActivity
    public DrawerView drawerView(AppCompatActivity appCompatActivity) {
        return new DrawerView(appCompatActivity);
    }

    @Provides
    public ActionBarDrawerToggle createToggle(Activity activity, DrawerView drawerView) {
        return new ActionBarDrawerToggle(activity, drawerView.drawer, drawerView.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }
}
