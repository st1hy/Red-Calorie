package com.github.st1hy.countthemcalories.ui.activities.settings.model;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.activities.settings.model.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.ui.activities.settings.model.unit.MassUnit;
import com.github.st1hy.countthemcalories.ui.activities.settings.model.unit.NamedUnit;
import com.github.st1hy.countthemcalories.ui.activities.settings.model.unit.VolumeUnit;

public enum SettingUnit {
    ENERGY(R.string.settings_unit_energy_title) {
        @Override
        public EnergyUnit[] options() {
            return EnergyUnit.values();
        }

        @Override
        public EnergyUnit getUnitFrom(@NonNull SettingsModel model) {
            return model.getEnergyUnit();
        }

        @Override
        public void setUnitTo(@NonNull SettingsModel model, int which) {
            model.setEnergyUnit(options()[which]);
        }
    },
    MASS(R.string.settings_unit_mass_title) {
        @Override
        public MassUnit[] options() {
            return MassUnit.values();
        }

        @Override
        public MassUnit getUnitFrom(@NonNull SettingsModel model) {
            return model.getMassUnit();
        }

        @Override
        public void setUnitTo(@NonNull SettingsModel model, int which) {
            model.setMassUnit(options()[which]);
        }
    },
    VOLUME(R.string.settings_unit_volume_title) {
        @Override
        public VolumeUnit[] options() {
            return VolumeUnit.values();
        }

        @Override
        public VolumeUnit getUnitFrom(@NonNull SettingsModel model) {
            return model.getVolumeUnit();
        }

        @Override
        public void setUnitTo(@NonNull SettingsModel model, int which) {
            model.setVolumeUnit(options()[which]);
        }
    },
    BODY_MASS(R.string.settings_unit_body_mass) {
        @Override
        public BodyMassUnit[] options() {
            return BodyMassUnit.values();
        }

        @Override
        public BodyMassUnit getUnitFrom(@NonNull SettingsModel model) {
            return model.getBodyMassUnit();
        }

        @Override
        public void setUnitTo(@NonNull SettingsModel model, int which) {
            model.setBodyMassUnit(options()[which]);
        }
    };

    private final int titleRes;

    SettingUnit(@StringRes int titleRes) {
        this.titleRes = titleRes;
    }

    public abstract NamedUnit[] options();

    public abstract NamedUnit getUnitFrom(@NonNull SettingsModel model);

    @StringRes
    public int getTitleRes() {
        return titleRes;
    }

    public abstract void setUnitTo(@NonNull SettingsModel model, int which);
}
