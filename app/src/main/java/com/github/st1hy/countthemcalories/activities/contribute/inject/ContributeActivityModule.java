package com.github.st1hy.countthemcalories.activities.contribute.inject;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.contribute.view.ContributeActivity;
import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenterImpl;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ContributeActivityModule {
    private final ContributeActivity activity;

    public ContributeActivityModule(@NonNull ContributeActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public DrawerPresenter provideDrawerPresenter() {
        return new DrawerPresenterImpl(activity, DrawerMenuItem.CONTRIBUTE);
    }

}
