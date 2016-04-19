package com.github.st1hy.countthemcalories.inject.component;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.github.st1hy.countthemcalories.OverviewActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.module.OverviewActivityModule;

import dagger.Component;

@PerActivity
@Component(modules = OverviewActivityModule.class, dependencies = ApplicationComponent.class)
public interface OverviewActivityComponent {

    @PerActivity
    Activity getActivity();

    @PerActivity
    DrawerLayout getDrawer();

    @PerActivity
    NavigationView getNavigationView();

    @PerActivity
    Toolbar getToolbar();

    @PerActivity
    FloatingActionButton getFab();

    @PerActivity
    ActionBarDrawerToggle getDrawerToggle();

    void inject(OverviewActivity activity);
}
