package com.github.st1hy.countthemcalories.core.drawer;

import android.app.Activity;
import android.support.v7.app.ActionBarDrawerToggle;

import com.github.st1hy.countthemcalories.R;

import dagger.Module;
import dagger.Provides;

@Module
public class DrawerModule {


    @Provides
    public ActionBarDrawerToggle createToggle(Activity activity, DrawerView drawerView) {
        return new ActionBarDrawerToggle(activity, drawerView.drawer, drawerView.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }
}
