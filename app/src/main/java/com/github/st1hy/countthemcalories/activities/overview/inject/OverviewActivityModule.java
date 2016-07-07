package com.github.st1hy.countthemcalories.activities.overview.inject;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewDrawerPresenter;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;
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
        return new OverviewDrawerPresenter(activity);
    }

    @PerActivity
    @Provides
    public OverviewFragment provideOverviewFragment() {
        return (OverviewFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.overview_content_fragment);
    }
}
