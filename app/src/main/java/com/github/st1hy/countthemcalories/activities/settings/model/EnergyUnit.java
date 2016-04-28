package com.github.st1hy.countthemcalories.activities.settings.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit;

public abstract class EnergyUnit extends SettingsChangedEvent {

    @NonNull
    public abstract EnergyDensityUnit getValue();

    public static class Mass extends EnergyUnit {
        private final GravimetricEnergyDensityUnit unit;
        public Mass(@NonNull GravimetricEnergyDensityUnit unit) {
            this.unit = unit;
        }

        @NonNull
        @Override
        public GravimetricEnergyDensityUnit getValue() {
            return unit;
        }
    }

    public static class Volume extends EnergyUnit {
        private final VolumetricEnergyDensityUnit unit;
        public Volume(@NonNull VolumetricEnergyDensityUnit unit) {
            this.unit = unit;
        }

        @NonNull
        @Override
        public VolumetricEnergyDensityUnit getValue() {
            return unit;
        }
    }
}
