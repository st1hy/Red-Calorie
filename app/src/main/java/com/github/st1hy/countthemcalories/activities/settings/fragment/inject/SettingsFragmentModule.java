package com.github.st1hy.countthemcalories.activities.settings.fragment.inject;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.settings.fragment.presenter.SettingsPresenter;
import com.github.st1hy.countthemcalories.activities.settings.fragment.presenter.SettingsPresenterImpl;
import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SettingsFragment;
import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SelectUnitViewHolder;
import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SettingsView;
import com.github.st1hy.countthemcalories.activities.settings.fragment.view.UnitsViewHolder;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.baseview.DialogView;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.google.common.base.Preconditions;

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
    @PerFragment
    public SettingsPresenterImpl providePresenterImpl(SettingsView view, SettingsModel model) {
        return new SettingsPresenterImpl(view, model);
    }


    @Provides
    public SettingsView provideView() {
        return fragment;
    }

    @Provides
    @PerFragment
    public UnitsViewHolder provideUnitsHolder() {
        View view = Preconditions.checkNotNull(fragment.getView());
        return new UnitsViewHolder(view);
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

    @Provides
    public DialogView provideDialogView() {
        FragmentActivity activity = fragment.getActivity();
        Preconditions.checkState(activity instanceof  DialogView, "activity must implement " + DialogView.class.getSimpleName());
        return (DialogView) activity;
    }

}
