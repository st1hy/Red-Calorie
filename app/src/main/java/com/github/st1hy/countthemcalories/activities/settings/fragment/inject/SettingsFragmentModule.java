package com.github.st1hy.countthemcalories.activities.settings.fragment.inject;

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


    @Provides
    public static SettingsPresenter providePresenter(SettingsPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    public static SettingsView provideView(SettingsViewImpl view) {
        return view;
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
    public static SelectUnitViewHolder provideMassHolder(UnitsViewHolder unitsViewHolder) {
        return new SelectUnitViewHolder(unitsViewHolder.getMass());
    }

    @Provides
    @PerFragment
    @Named("volume")
    public static SelectUnitViewHolder provideVolumeHolder(UnitsViewHolder unitsViewHolder) {
        return new SelectUnitViewHolder(unitsViewHolder.getVolume());
    }

    @Provides
    @PerFragment
    @Named("bodyMass")
    public static SelectUnitViewHolder providebodyMassHolder(UnitsViewHolder unitsViewHolder) {
        return new SelectUnitViewHolder(unitsViewHolder.getBodyMass());
    }

}
