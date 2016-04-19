package com.github.st1hy.countthemcalories.inject.module;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.st1hy.countthemcalories.AddMealActivity;
import com.github.st1hy.countthemcalories.OverviewActivity;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Module;
import dagger.Provides;

@Module
public class OverviewActivityModule {
    private OverviewActivity activity;

    @Bind(R.id.overview_toolbar)
    Toolbar toolbar;
    @Bind(R.id.overview_fab)
    FloatingActionButton fab;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    public OverviewActivityModule(OverviewActivity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
    }

    @PerActivity
    @Provides
    public Activity provideActivity() {
        return activity;
    }

    @PerActivity
    @Provides
    public NavigationView provideNavigationView() {
        return navigationView;
    }

    @PerActivity
    @Provides
    public DrawerLayout provideDrawer() {
        return drawer;
    }


    @PerActivity
    @Provides
    public FloatingActionButton provideFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, AddMealActivity.class));
            }
        });
        return fab;
    }

    @PerActivity
    @Provides
    public Toolbar provideToolbar() {
        activity.setSupportActionBar(toolbar);
        return toolbar;
    }

    @PerActivity
    @Provides
    public ActionBarDrawerToggle provideDrawerToggle() {
        return new ActionBarDrawerToggle(activity, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }
}
