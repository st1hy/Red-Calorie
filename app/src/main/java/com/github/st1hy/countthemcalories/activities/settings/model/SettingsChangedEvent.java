package com.github.st1hy.countthemcalories.activities.settings.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class SettingsChangedEvent {

    @Nullable
    public static SettingsChangedEvent from(@NonNull SettingsModel model, @NonNull String key) {
        switch (key) {
            case SettingsModel.PREFERRED_MASS_ENERGY_UNIT:
                return new EnergyUnit.Mass(model.getPreferredGravimetricUnit());
            case SettingsModel.PREFERRED_VOLUME_ENERGY_UNIT:
                return new EnergyUnit.Volume(model.getPreferredVolumetricUnit());
            default:
                return null;
        }
    }

}
