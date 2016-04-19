package com.github.st1hy.countthemcalories.activities.overview.inject;

import android.app.Activity;

import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewPresenterImp;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewView;

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
    public Activity provideActivity() {
        return activity;
    }

    @PerActivity
    @Provides
    public OverviewView provideView() {
        return activity;
    }

    @PerActivity
    @Provides
    public OverviewPresenter providePresenter(OverviewPresenterImp presenter) {
        return presenter;
    }
}
