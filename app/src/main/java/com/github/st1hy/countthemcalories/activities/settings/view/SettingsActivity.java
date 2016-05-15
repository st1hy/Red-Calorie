package com.github.st1hy.countthemcalories.activities.settings.view;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.inject.DaggerSettingsActivityComponent;
import com.github.st1hy.countthemcalories.activities.settings.inject.SettingsActivityComponent;
import com.github.st1hy.countthemcalories.activities.settings.inject.SettingsActivityModule;
import com.github.st1hy.countthemcalories.activities.settings.presenter.SettingsPresenter;
import com.github.st1hy.countthemcalories.activities.settings.view.holder.SelectUnitViewHolder;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerActivity;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;

public class SettingsActivity extends DrawerActivity implements SettingsView {
    SettingsActivityComponent component;

    @Inject SettingsPresenter presenter;

    @Inject @Named("energy") SelectUnitViewHolder energyHolder;
    @Inject @Named("mass") SelectUnitViewHolder massHolder;
    @Inject @Named("volume") SelectUnitViewHolder volumeHolder;

    @NonNull
    protected SettingsActivityComponent getComponent() {
        if (component == null) {
            component = DaggerSettingsActivityComponent.builder()
                    .applicationComponent(getAppComponent())
                    .settingsActivityModule(new SettingsActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        getComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    public SelectUnitViewHolder getEnergyHolder() {
        return energyHolder;
    }

    @NonNull
    public SelectUnitViewHolder getMassHolder() {
        return massHolder;
    }

    @NonNull
    public SelectUnitViewHolder getVolumeHolder() {
        return volumeHolder;
    }
}
