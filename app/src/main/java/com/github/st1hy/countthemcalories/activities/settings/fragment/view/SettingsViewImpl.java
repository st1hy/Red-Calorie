package com.github.st1hy.countthemcalories.activities.settings.fragment.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;
import javax.inject.Named;

@PerFragment
public class SettingsViewImpl implements SettingsView {

    @NonNull
    @Named("energy")
    private final SelectUnitViewHolder energyHolder;
    @NonNull
    @Named("mass")
    private final SelectUnitViewHolder massHolder;
    @NonNull
    @Named("volume")
    private final SelectUnitViewHolder volumeHolder;

    @Inject
    public SettingsViewImpl(@NonNull @Named("energy") SelectUnitViewHolder energyHolder,
                            @NonNull @Named("mass") SelectUnitViewHolder massHolder,
                            @NonNull @Named("volume") SelectUnitViewHolder volumeHolder) {
        this.energyHolder = energyHolder;
        this.massHolder = massHolder;
        this.volumeHolder = volumeHolder;
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
