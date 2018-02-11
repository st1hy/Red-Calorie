package com.github.st1hy.countthemcalories.ui.inject.settings;

import com.github.st1hy.countthemcalories.ui.activities.settings.presenter.SettingsPresenter;
import com.github.st1hy.countthemcalories.ui.activities.settings.presenter.SettingsPresenterImpl;
import com.github.st1hy.countthemcalories.ui.activities.settings.view.SelectUnitViewHolder;
import com.github.st1hy.countthemcalories.ui.activities.settings.view.SettingsView;
import com.github.st1hy.countthemcalories.ui.activities.settings.view.SettingsViewImpl;
import com.github.st1hy.countthemcalories.ui.activities.settings.view.UnitsViewHolder;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

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
