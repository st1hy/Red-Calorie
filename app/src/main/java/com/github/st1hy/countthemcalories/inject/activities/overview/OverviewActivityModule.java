package com.github.st1hy.countthemcalories.inject.activities.overview;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreenImpl;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.OverviewFragmentComponentFactory;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class OverviewActivityModule {
    private OverviewActivity activity;

    public OverviewActivityModule(OverviewActivity activity) {
        this.activity = activity;
    }

    @Provides
    public OverviewFragment provideOverviewFragment(FragmentManager fragmentManager,
                                                    OverviewFragmentComponentFactory componentFactory) {
        OverviewFragment fragment = (OverviewFragment) fragmentManager
                .findFragmentById(R.id.overview_content_fragment);
        fragment.setComponentFactory(componentFactory);
        return fragment;
    }

    @Provides
    public FragmentManager fragmentManager() {
        return activity.getSupportFragmentManager();
    }

    @Provides
    @PerActivity
    @Named("undoViewRoot")
    public View rootUndoView(Activity activity) {
        return activity.findViewById(R.id.overview_root);
    }

    @Provides
    @Named("activityContext")
    public Context activityContext() {
        return activity;
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
        return DrawerMenuItem.OVERVIEW;
    }

    @Provides
    public OverviewScreen overviewScreen(OverviewScreenImpl screen) {
        return screen;
    }

    @Provides
    public OverviewFragmentComponentFactory fragmentComponentFactory(OverviewActivityComponent component) {
        return component;
    }
}
