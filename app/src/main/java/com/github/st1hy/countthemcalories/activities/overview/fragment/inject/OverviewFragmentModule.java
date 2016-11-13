package com.github.st1hy.countthemcalories.activities.overview.fragment.inject;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.OverviewPresenterImp;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewViewImpl;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class OverviewFragmentModule {

    final OverviewFragment fragment;

    public OverviewFragmentModule(@NonNull OverviewFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    public OverviewView provideView(OverviewViewImpl view) {
        return view;
    }

    @PerFragment
    @Provides
    public OverviewPresenter provideDrawerPresenter(OverviewPresenterImp presenter) {
        return presenter;
    }

    @PerFragment
    @Provides
    public Resources provideResources() {
        return fragment.getResources();
    }

    @Provides
    public FragmentActivity provideFragmentActivity() {
        return fragment.getActivity();
    }

    @Provides
    public View rootView() {
        return fragment.getView();
    }
}
