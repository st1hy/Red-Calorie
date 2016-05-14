package com.github.st1hy.countthemcalories.activities.overview.inject;

import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewPresenterImp;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewView;
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
    public OverviewView provideView() {
        return activity;
    }

    @PerActivity
    @Provides
    public OverviewPresenterImp providePresenter(OverviewView view) {
        return new OverviewPresenterImp(view);
    }

    @PerActivity
    @Provides
    public OverviewPresenter provideOverviewPresenter(OverviewPresenterImp presenter) {
        return presenter;
    }

    @PerActivity
    @Provides
    public DrawerPresenter provideDrawerPresenter(OverviewPresenterImp presenter) {
        return presenter;
    }

}
