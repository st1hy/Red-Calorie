package com.github.st1hy.countthemcalories.activities.contribute.inject;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.contribute.presenter.ContributePresenter;
import com.github.st1hy.countthemcalories.activities.contribute.view.ContributeActivity;
import com.github.st1hy.countthemcalories.activities.contribute.view.ContributeView;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;
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
    public DrawerPresenter provideDrawerPresenter(ContributePresenter presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public ContributeView provideView() {
        return activity;
    }

}
