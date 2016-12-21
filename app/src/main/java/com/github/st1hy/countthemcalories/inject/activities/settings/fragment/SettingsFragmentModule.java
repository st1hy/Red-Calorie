package com.github.st1hy.countthemcalories.inject.activities.settings.fragment;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.settings.fragment.SettingsFragment;
import com.github.st1hy.countthemcalories.activities.settings.fragment.presenter.SettingsPresenter;
import com.github.st1hy.countthemcalories.activities.settings.fragment.presenter.SettingsPresenterImpl;
import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SelectUnitViewHolder;
import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SettingsView;
import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SettingsViewImpl;
import com.github.st1hy.countthemcalories.activities.settings.fragment.view.UnitsViewHolder;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsFragmentModule {
    private final SettingsFragment fragment;

    public SettingsFragmentModule(@NonNull SettingsFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    public SettingsPresenter providePresenter(SettingsPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    public SettingsView provideView(SettingsViewImpl view) {
        return view;
    }

    @Provides
    public View rootView() {
        return fragment.getView();
    }

    @Provides
    @PerFragment
    @Named("energy")
    public SelectUnitViewHolder provideEnergyHolder(UnitsViewHolder unitsViewHolder) {
        return new SelectUnitViewHolder(unitsViewHolder.getEnergy());
    }


    @Provides
    @PerFragment
    @Named("mass")
    public SelectUnitViewHolder provideMassHolder(UnitsViewHolder unitsViewHolder) {
        return new SelectUnitViewHolder(unitsViewHolder.getMass());
    }

    @Provides
    @PerFragment
    @Named("volume")
    public SelectUnitViewHolder provideVolumeHolder(UnitsViewHolder unitsViewHolder) {
        return new SelectUnitViewHolder(unitsViewHolder.getVolume());
    }

}
