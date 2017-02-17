package com.github.st1hy.countthemcalories.inject.activities.overview;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.graph.GraphFragment;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.GraphComponentFactory;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = OverviewActivityBindings.class)
public class OverviewActivityModule {
    private OverviewActivity activity;

    public OverviewActivityModule(OverviewActivity activity) {
        this.activity = activity;
    }

    @Provides
    public static GraphFragment provideGraphFragment(
            FragmentManager fragmentManager,
            GraphComponentFactory componentFactory) {

        GraphFragment fragment = (GraphFragment) fragmentManager
                .findFragmentById(R.id.overview_graph_fragment);
        fragment.setComponentFactory(componentFactory);
        return fragment;
    }

    @Provides
    public static FragmentManager fragmentManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @Provides
    @PerActivity
    @Named("undoViewRoot")
    public static View rootUndoView(Activity activity) {
        return activity.findViewById(R.id.overview_root);
    }

    @Provides
    public AppCompatActivity appCompatActivity() {
        return activity;
    }

    @Provides
    public static DrawerMenuItem drawerMenuItem() {
        return DrawerMenuItem.OVERVIEW;
    }

}
