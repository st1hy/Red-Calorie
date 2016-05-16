package com.github.st1hy.countthemcalories.activities.settings.inject;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.activities.settings.presenter.SettingsPresenter;
import com.github.st1hy.countthemcalories.activities.settings.presenter.SettingsPresenterImpl;
import com.github.st1hy.countthemcalories.activities.settings.view.SelectUnitViewHolder;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsView;
import com.github.st1hy.countthemcalories.activities.settings.view.UnitsViewHolder;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsActivityModule {
    private final SettingsActivity activity;

    public SettingsActivityModule(@NonNull SettingsActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public SettingsPresenter providePresenter(SettingsPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public SettingsPresenterImpl providePresenterImpl(SettingsView view, SettingsModel model) {
        return new SettingsPresenterImpl(view, model);
    }

    @Provides
    @PerActivity
    public DrawerPresenter provideDrawerPresenter(SettingsPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public SettingsView provideView() {
        return activity;
    }

    @Provides
    @PerActivity
    public UnitsViewHolder provideUnitsHolder() {
        return new UnitsViewHolder(activity);
    }

    @Provides
    @PerActivity
    @Named("energy")
    public SelectUnitViewHolder provideEnergyHolder(UnitsViewHolder unitsViewHolder) {
        return new SelectUnitViewHolder(unitsViewHolder.getEnergy());
    }


    @Provides
    @PerActivity
    @Named("mass")
    public SelectUnitViewHolder provideMassHolder(UnitsViewHolder unitsViewHolder) {
        return new SelectUnitViewHolder(unitsViewHolder.getMass());
    }

    @Provides
    @PerActivity
    @Named("volume")
    public SelectUnitViewHolder provideVolumeHolder(UnitsViewHolder unitsViewHolder) {
        return new SelectUnitViewHolder(unitsViewHolder.getVolume());
    }

}
