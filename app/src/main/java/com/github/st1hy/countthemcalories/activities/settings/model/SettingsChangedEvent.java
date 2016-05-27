package com.github.st1hy.countthemcalories.activities.settings.model;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Subscriber;
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
            default:
                return null;
        }
    }

    @NonNull
    public static Observable<SettingsChangedEvent> create(@NonNull final SettingsModel model) {

        return Observable.create(new Observable.OnSubscribe<SettingsChangedEvent>() {
            @Override
            public void call(final Subscriber<? super SettingsChangedEvent> subscriber) {
                final OnSharedPreferenceChangeListener preferenceChangeListener = new OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        if (!subscriber.isUnsubscribed()) {
                            SettingsChangedEvent event = SettingsChangedEvent.from(model, key);
                            if (event != null) {
                                subscriber.onNext(event);
                            }
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
            }
        });
    }

}
