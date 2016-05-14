package com.github.st1hy.countthemcalories.activities.settings.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.Unit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;

public abstract class UnitChangedEvent extends SettingsChangedEvent {

    @NonNull
    public abstract Unit getUnit();

    @NonNull
    public abstract SettingUnit getSetting();

    public static class Energy extends UnitChangedEvent {
        private final EnergyUnit unit;
        public Energy(@NonNull EnergyUnit unit) {
            this.unit = unit;
        }

        @NonNull
        public EnergyUnit getUnit() {
            return unit;
        }

        @NonNull
        @Override
        public SettingUnit getSetting() {
            return SettingUnit.ENERGY;
        }
    }
    public static class Mass extends UnitChangedEvent {
        private final MassUnit unit;

        public Mass(@NonNull MassUnit unit) {
            this.unit = unit;
        }
        @NonNull
        public MassUnit getUnit() {
            return unit;
        }

        @NonNull
        @Override
        public SettingUnit getSetting() {
            return SettingUnit.MASS;
        }

    }
    public static class Volume extends UnitChangedEvent {
        private final VolumeUnit unit;

        public Volume(@NonNull VolumeUnit unit) {
            this.unit = unit;
        }
        @NonNull
        public VolumeUnit getUnit() {
            return unit;
        }

        @NonNull
        @Override
        public SettingUnit getSetting() {
            return SettingUnit.VOLUME;
        }
    }
}
