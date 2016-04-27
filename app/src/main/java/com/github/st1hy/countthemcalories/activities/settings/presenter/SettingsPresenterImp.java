package com.github.st1hy.countthemcalories.activities.settings.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.settings.view.SettingsView;

import javax.inject.Inject;

public class SettingsPresenterImp implements SettingsPresenter {
    private final SettingsView view;

    @Inject
    public SettingsPresenterImp(@NonNull SettingsView view) {
        this.view = view;
    }
}
