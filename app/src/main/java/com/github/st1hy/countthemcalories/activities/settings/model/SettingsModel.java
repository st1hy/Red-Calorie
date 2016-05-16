package com.github.st1hy.countthemcalories.activities.settings.model;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.Unit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;

import javax.inject.Inject;

import rx.Observable;

public class SettingsModel {
    static final String PREFERRED_ENERGY_UNIT = "setting preferred energy unit";
    static final String PREFERRED_MASS_UNIT = "setting preferred mass unit";
    static final String PREFERRED_VOLUME_UNIT = "setting preferred volume unit";

    static final EnergyUnit defaultUnitOfEnergy = EnergyUnit.KCAL;
    static final MassUnit defaultUnitOfMass = MassUnit.G100;
    static final VolumeUnit defaultUnitOfVolume = VolumeUnit.ML100;

    private final SharedPreferences preferences;
    private final Resources resources;

    private final Observable<SettingsChangedEvent> observable;

    @Inject
    public SettingsModel(@NonNull final SharedPreferences preferences,
                         @NonNull Resources resources) {
        this.preferences = preferences;
        this.resources = resources;
        observable = SettingsChangedEvent.create(this);
    }

    public void setEnergyUnit(@NonNull EnergyUnit unit) {
        preferences.edit().putInt(PREFERRED_ENERGY_UNIT, unit.getId()).apply();
    }

    @NonNull
    public EnergyUnit getEnergyUnit() {
        int id = preferences.getInt(PREFERRED_ENERGY_UNIT, -1);
        EnergyUnit unit = EnergyUnit.fromId(id);
        if (unit != null) {
            return unit;
        } else {
            return defaultUnitOfEnergy;
        }
    }

    public void setMassUnit(@NonNull MassUnit unit) {
        preferences.edit().putInt(PREFERRED_MASS_UNIT, unit.getId()).apply();
    }

    @NonNull
    public MassUnit getMassUnit() {
        int id = preferences.getInt(PREFERRED_MASS_UNIT, -1);
        MassUnit unit = MassUnit.fromId(id);
        if (unit != null) {
            return unit;
        } else {
            return defaultUnitOfMass;
        }
    }

    public void setVolumeUnit(@NonNull VolumeUnit unit) {
        preferences.edit().putInt(PREFERRED_VOLUME_UNIT, unit.getId()).apply();
    }

    @NonNull
    public VolumeUnit getVolumeUnit() {
        int id = preferences.getInt(PREFERRED_VOLUME_UNIT, -1);
        VolumeUnit unit = VolumeUnit.fromId(id);
        if (unit != null) {
            return unit;
        } else {
            return defaultUnitOfVolume;
        }
    }

    public void resetToDefaultSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREFERRED_ENERGY_UNIT, defaultUnitOfEnergy.getId());
        editor.putInt(PREFERRED_MASS_UNIT, defaultUnitOfMass.getId());
        editor.putInt(PREFERRED_VOLUME_UNIT, defaultUnitOfVolume.getId());
        editor.apply();
    }

    @NonNull
    public String getUnitName(@NonNull Unit unit) {
        return resources.getString(unit.getNameRes());
    }

    @NonNull
    public Observable<SettingsChangedEvent> toObservable() {
        return observable;
    }

    @StringRes
    public int getPreferredUnitDialogTitle() {
        return R.string.settings_select_unit_dialog_title;
    }


    @NonNull
    public AmountUnit getAmountUnitFrom(@NonNull AmountUnitType type) {
        switch (type) {
            case VOLUME:
                return getVolumeUnit();
            case MASS:
                return getMassUnit();
            case UNKNOWN:
            default:
                throw new UnsupportedOperationException();
        }
    }

    @NonNull
    public SharedPreferences getPreferences() {
        return preferences;
    }
}
