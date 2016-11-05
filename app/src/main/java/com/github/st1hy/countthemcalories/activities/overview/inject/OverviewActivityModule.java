package com.github.st1hy.countthemcalories.activities.overview.inject;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenterImpl;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class OverviewActivityModule {
    private OverviewActivity activity;

    public OverviewActivityModule(OverviewActivity activity) {
        this.activity = activity;
    }

    @PerActivity
    @Provides
    public DrawerPresenter provideDrawerPresenter() {
        return new DrawerPresenterImpl(activity, DrawerMenuItem.OVERVIEW);
    }

    @Provides
    public OverviewFragment provideOverviewFragment() {
        return (OverviewFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.overview_content_fragment);
    }
}
