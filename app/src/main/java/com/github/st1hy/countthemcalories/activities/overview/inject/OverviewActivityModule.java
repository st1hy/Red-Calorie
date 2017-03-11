package com.github.st1hy.countthemcalories.activities.overview.inject;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.graph.GraphFragment;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.GraphComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.mealpager.inject.MealsPagerModule;
import com.github.st1hy.countthemcalories.activities.overview.meals.inject.OverviewFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreenImpl;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.core.ActivityLauncherModule;
import com.github.st1hy.countthemcalories.inject.core.DrawerModule;
import com.github.st1hy.countthemcalories.inject.core.UndoModule;
import com.github.st1hy.countthemcalories.inject.quantifier.datetime.NewMealDate;

import org.joda.time.DateTime;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        DrawerModule.class,
        UndoModule.class,
        ActivityLauncherModule.class,
        MealsPagerModule.class
})
public abstract class OverviewActivityModule {

    @Binds
    public abstract OverviewScreen overviewScreen(OverviewScreenImpl screen);

    @Binds
    public abstract OverviewFragmentComponentFactory fragmentComponentFactory(OverviewActivityComponent component);

    @Binds
    public abstract GraphComponentFactory graphComponentFactory(OverviewActivityComponent component);

    @Provides
    public static GraphFragment provideGraphFragment(FragmentManager fragmentManager,
                                                     GraphComponentFactory componentFactory) {

        GraphFragment fragment = (GraphFragment) fragmentManager
                .findFragmentById(R.id.overview_graph_fragment);
        fragment.setComponentFactory(componentFactory);
        return fragment;
    }

    @Provides
    @PerActivity
    @Named("undoViewRoot")
    public static View rootUndoView(Activity activity) {
        return activity.findViewById(R.id.overview_root);
    }

    @Provides
    public static DrawerMenuItem drawerMenuItem() {
        return DrawerMenuItem.OVERVIEW;
    }


    @Provides
    @Nullable
    @NewMealDate
    public static DateTime jumpToDate(Intent intent) {
        DateTime date = (DateTime) intent.getSerializableExtra(OverviewActivity.EXTRA_JUMP_TO_DATE);
        intent.removeExtra(OverviewActivity.EXTRA_JUMP_TO_DATE);
        return date;
    }

}
