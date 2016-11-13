package com.github.st1hy.countthemcalories.activities.overview.inject;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreenImpl;
import com.github.st1hy.countthemcalories.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.core.command.undo.UndoViewImpl;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class OverviewActivityModule {
    private OverviewActivity activity;

    public OverviewActivityModule(OverviewActivity activity) {
        this.activity = activity;
    }

    @Provides
    public OverviewFragment provideOverviewFragment() {
        return (OverviewFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.overview_content_fragment);
    }

    @Provides
    @PerActivity
    public View rootUndoView(Activity activity) {
        return activity.findViewById(R.id.overview_root);
    }

    @PerActivity
    @Provides
    public UndoView undoView(View rootUndoView) {
        return new UndoViewImpl(rootUndoView);
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
}
