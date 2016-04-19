package com.github.st1hy.countthemcalories.inject.module;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.github.st1hy.countthemcalories.IngredientsActivity;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Module;
import dagger.Provides;

@Module
public class IngredientsActivityModule {
    private final IngredientsActivity activity;

    @Bind(R.id.ingredients_toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    public IngredientsActivityModule(IngredientsActivity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
    }

    @Provides
    @PerActivity
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
    public Toolbar provideToolbar() {
        return toolbar;
    }

}
