package com.github.st1hy.countthemcalories.ui.inject.core;

import android.app.Activity;
import android.support.v7.app.ActionBarDrawerToggle;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.core.drawer.DrawerView;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class DrawerModule {

    @Provides
    public static ActionBarDrawerToggle createToggle(Activity activity, DrawerView drawerView) {
        return new ActionBarDrawerToggle(activity, drawerView.getDrawer(), drawerView.getToolbar(),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }
}
