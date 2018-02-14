package com.github.st1hy.countthemcalories.ui.activities.overview.inject;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.GraphFragment;
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject.GraphComponentFactory;
import com.github.st1hy.countthemcalories.ui.activities.overview.mealpager.inject.MealsPagerModule;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.inject.OverviewFragmentComponentFactory;
import com.github.st1hy.countthemcalories.ui.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.ui.activities.overview.view.OverviewScreenImpl;
import com.github.st1hy.countthemcalories.ui.core.FragmentLocation;
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityLauncherModule;
import com.github.st1hy.countthemcalories.ui.core.command.undo.inject.UndoModule;
import com.github.st1hy.countthemcalories.ui.core.command.undo.inject.UndoRootView;
import com.github.st1hy.countthemcalories.ui.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.DrawerModule;
import com.github.st1hy.countthemcalories.ui.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.datetime.NewMealDate;

import org.joda.time.DateTime;

import java.util.Map;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module(includes = {
        DrawerModule.class,
        UndoModule.class,
        ActivityLauncherModule.class,
        MealsPagerModule.class,
        PermissionModule.class,
})
public abstract class OverviewActivityModule {

    private static final String GRAPH_CONTENT_TAG = "overview_graph_fragment";

    @Binds
    public abstract OverviewScreen overviewScreen(OverviewScreenImpl screen);

    @Binds
    public abstract OverviewFragmentComponentFactory fragmentComponentFactory(OverviewActivityComponent component);

    @Binds
    public abstract GraphComponentFactory graphComponentFactory(OverviewActivityComponent component);

    @Provides
    @IntoMap
    @StringKey(GRAPH_CONTENT_TAG)
    @Reusable
    public static FragmentLocation graphFragment(GraphComponentFactory componentFactory) {
        return new FragmentLocation.Builder<>(GraphFragment.class, GRAPH_CONTENT_TAG)
                .setViewRootId(R.id.overview_content_root)
                .setInjector(fragment -> fragment.setComponentFactory(componentFactory))
                .build();
    }

    @Provides
    public static GraphFragment provideGraphFragment(Map<String, Fragment> fragments) {
        return (GraphFragment) fragments.get(GRAPH_CONTENT_TAG);
    }

    @Provides
    @PerActivity
    @UndoRootView
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
