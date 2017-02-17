package com.github.st1hy.countthemcalories.activities.settings.model;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.android.MainThreadSubscription;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public abstract class SettingsChangedEvent {

    @Nullable
    public static SettingsChangedEvent from(@NonNull SettingsModel model, @NonNull String key) {
        switch (key) {
            case SettingsModel.PREFERRED_ENERGY_UNIT:
                return new UnitChangedEvent.Energy(model.getEnergyUnit());
            case SettingsModel.PREFERRED_MASS_UNIT:
                return new UnitChangedEvent.Mass(model.getMassUnit());
            case SettingsModel.PREFERRED_VOLUME_UNIT:
                return new UnitChangedEvent.Volume(model.getVolumeUnit());
            case SettingsModel.PREFERRED_BODY_MASS_UNIT:
                return new UnitChangedEvent.BodyMass(model.getBodyMassUnit());
            default:
                return null;
        }
    }

    @NonNull
    public static Observable<SettingsChangedEvent> create(@NonNull final SettingsModel model) {

        return Observable.create(subscriber -> {
            final OnSharedPreferenceChangeListener preferenceChangeListener =
                    (sharedPreferences, key) -> {
                        if (!subscriber.isUnsubscribed()) {
                            SettingsChangedEvent event = SettingsChangedEvent.from(model, key);
                            if (event != null) {
                                subscriber.onNext(event);
                            }
                        }
                    };
            final SharedPreferences preferences = model.getPreferences();
            preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
                }
            });
        });
    }

}
