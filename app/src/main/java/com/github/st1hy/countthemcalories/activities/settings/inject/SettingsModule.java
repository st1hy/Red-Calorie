package com.github.st1hy.countthemcalories.activities.settings.inject;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.settings.presenter.SettingsPresenter;
import com.github.st1hy.countthemcalories.activities.settings.presenter.SettingsPresenterImp;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsView;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsModule {
    private final SettingsActivity activity;

    public SettingsModule(@NonNull SettingsActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public Activity provideActivity() {
        return activity;
    }

    @Provides
    @PerActivity
    public SettingsView provideView() {
        return activity;
    }

    @Provides
    @PerActivity
    public SettingsPresenter providePresenter(SettingsPresenterImp presenter) {
        return presenter;
    }
}
