package com.github.st1hy.countthemcalories.inject.activities.overview;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.graph.GraphFragment;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.GraphComponentFactory;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.overview.quantifier.datetime.NewMealDate;

import org.joda.time.DateTime;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = OverviewActivityBindings.class)
public class OverviewActivityModule {
    public static final String EXTRA_JUMP_TO_DATE = "extra jump to date";
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

    @Provides
    public static Intent intent(Activity activity) {
        return activity.getIntent();
    }

    @Provides
    @Nullable
    @NewMealDate
    public static DateTime jumpToDate(Intent intent) {
        DateTime date = (DateTime) intent.getSerializableExtra(EXTRA_JUMP_TO_DATE);
        intent.removeExtra(EXTRA_JUMP_TO_DATE);
        return date;
    }

}
