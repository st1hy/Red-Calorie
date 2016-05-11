package com.github.st1hy.countthemcalories.activities.settings.model;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.Observable;

import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KCAL_AT_100G;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KCAL_AT_100ML;

public class SettingsModel {
    static final String PREFERRED_VOLUME_ENERGY_UNIT = "setting preferred volumetric energy unit";
    static final String PREFERRED_MASS_ENERGY_UNIT = "setting preferred gravimetric energy unit";
    static final GravimetricEnergyDensityUnit defaultGravimetricEnergyUnit = KCAL_AT_100G;
    static final VolumetricEnergyDensityUnit defaultVolumetricEnergyUnit = KCAL_AT_100ML;

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

    public void setPreferredVolumetricUnit(@NonNull VolumetricEnergyDensityUnit unit) {
        preferences.edit().putInt(PREFERRED_VOLUME_ENERGY_UNIT, unit.getId()).apply();
    }

    @NonNull
    public VolumetricEnergyDensityUnit getPreferredVolumetricUnit() {
        int id = preferences.getInt(PREFERRED_VOLUME_ENERGY_UNIT, -1);
        if (id != -1) {
            return VolumetricEnergyDensityUnit.fromId(id);
        } else {
            return defaultVolumetricEnergyUnit;
        }
    }

    public void setPreferredGravimetricUnit(@NonNull GravimetricEnergyDensityUnit unit) {
        preferences.edit().putInt(PREFERRED_MASS_ENERGY_UNIT, unit.getId()).apply();
    }

    @NonNull
    public GravimetricEnergyDensityUnit getPreferredGravimetricUnit() {
        int id = preferences.getInt(PREFERRED_MASS_ENERGY_UNIT, -1);
        if (id != -1) {
            return GravimetricEnergyDensityUnit.fromId(id);
        } else {
            return defaultGravimetricEnergyUnit;
        }
    }

    public void setDefaultSettingsIfNotSet() {
        boolean setVolumeEnergyUnit = preferences.getInt(PREFERRED_VOLUME_ENERGY_UNIT, -1) == -1;
        boolean setMassEnergyUnit = preferences.getInt(PREFERRED_MASS_ENERGY_UNIT, -1) == -1;

        if (setVolumeEnergyUnit || setMassEnergyUnit) {
            SharedPreferences.Editor editor = preferences.edit();
            if (setVolumeEnergyUnit)
                editor.putInt(PREFERRED_VOLUME_ENERGY_UNIT, defaultVolumetricEnergyUnit.getId());
            if (setMassEnergyUnit)
                editor.putInt(PREFERRED_MASS_ENERGY_UNIT, defaultGravimetricEnergyUnit.getId());
            editor.apply();
        }
    }

    public void resetToDefaultSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREFERRED_VOLUME_ENERGY_UNIT, defaultVolumetricEnergyUnit.getId());
        editor.putInt(PREFERRED_MASS_ENERGY_UNIT, defaultGravimetricEnergyUnit.getId());
        editor.apply();
    }

    @NonNull
    public String getUnitName(@NonNull EnergyDensityUnit unit, @NonNull BigDecimal decimal) {
        return resources.getString(unit.getFormatResId(), decimal.toPlainString());
    }

    @NonNull
    public String getUnitName(@NonNull EnergyDensity energyDensity) {
        EnergyDensityUnit unit = energyDensity.getUnit();
        BigDecimal decimal = energyDensity.getValue()
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros();
        return getUnitName(unit, decimal);
    }

    @NonNull
    public String getUnitName(@NonNull EnergyDensityUnit unit) {
        return resources.getString(unit.getNameResId());
    }


    public Observable<SettingsChangedEvent> toObservable() {
        return observable;
    }

    @StringRes
    public int getPreferredUnitDialogTitle() {
        return R.string.settings_select_unit_dialog_title;
    }


    @NonNull
    public EnergyDensityUnit getPreferredFrom(@NonNull AmountUnitType type) {
        switch (type) {
            case VOLUME:
                return getPreferredVolumetricUnit();
            case MASS:
                return getPreferredGravimetricUnit();
            default:
                throw new UnsupportedOperationException();
        }
    }

    @NonNull
    public SharedPreferences getPreferences() {
        return preferences;
    }
}
