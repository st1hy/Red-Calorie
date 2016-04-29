package com.github.st1hy.countthemcalories.activities.settings.model;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action0;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KCAL_AT_100G;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KCAL_AT_100ML;

public class SettingsModel implements SharedPreferences.OnSharedPreferenceChangeListener {
    static final String PREFERRED_VOLUME_ENERGY_UNIT = "setting preferred volumetric energy unit";
    static final String PREFERRED_MASS_ENERGY_UNIT = "setting preferred gravimetric energy unit";
    static final GravimetricEnergyDensityUnit defaultGravimetricEnergyUnit = KCAL_AT_100G;
    static final VolumetricEnergyDensityUnit defaultVolumetricEnergyUnit = KCAL_AT_100ML;

    private final SharedPreferences preferences;
    private final Resources resources;

    private final Subject<SettingsChangedEvent, SettingsChangedEvent> subject = new SerializedSubject<>(PublishSubject.<SettingsChangedEvent>create());
    private final Observable<SettingsChangedEvent> observable;

    @Inject
    public SettingsModel(@NonNull final SharedPreferences preferences,
                         @NonNull Resources resources) {
        this.preferences = preferences;
        this.resources = resources;
        observable = subject.doOnSubscribe(new Action0() {

            @Override
            public void call() {
                preferences.registerOnSharedPreferenceChangeListener(SettingsModel.this);
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                preferences.unregisterOnSharedPreferenceChangeListener(SettingsModel.this);
            }
        }).share();
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
    public String getUnitPlural(@NonNull EnergyDensityUnit unit, int amount) {
        return resources.getQuantityString(unit.getPluralResId(), amount, amount);
    }

    @NonNull
    public String getUnitName(@NonNull EnergyDensityUnit unit) {
        String unitName = getUnitPlural(unit, 1);
        if (unitName.startsWith("1")) {
            int indexOf1 = unitName.indexOf("1");
            unitName = unitName.substring(indexOf1 + 1, unitName.length()).trim();
        }
        return unitName;
    }


    public Observable<SettingsChangedEvent> toObservable() {
        return observable;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SettingsChangedEvent event = SettingsChangedEvent.from(this, key);
        if (event != null) {
            subject.onNext(event);
        }
    }

    @StringRes
    public int getPreferredUnitDialogTitle() {
        return R.string.settings_select_unit_dialog_title;
    }
}
