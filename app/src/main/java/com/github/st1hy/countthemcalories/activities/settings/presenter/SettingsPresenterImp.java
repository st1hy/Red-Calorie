package com.github.st1hy.countthemcalories.activities.settings.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.settings.model.EnergyUnit;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsChangedEvent;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsView;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class SettingsPresenterImp implements SettingsPresenter {
    final SettingsView view;
    final SettingsModel model;

    final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public SettingsPresenterImp(@NonNull SettingsView view,
                                @NonNull SettingsModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onLiquidUnitSettingsClicked() {
        onUnitSettingsClicked(AmountUnitType.VOLUME);
    }


    private void onUnitSettingsClicked(@NonNull final AmountUnitType type) {
        EnergyDensityUnit[] units = (EnergyDensityUnit[]) EnergyDensityUtils.getUnits(type);
        String[] values = new String[units.length];
        for (int i = 0; i < units.length; i++) {
            values[i] = model.getUnitPlural(units[i], 1);
        }
        view.showAlertDialog(model.getPreferredUnitDialogTitle(), values)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer which) {
                        onSelectedUnitType(type, which);
                    }
                });
    }

    void onSelectedUnitType(@NonNull AmountUnitType type, int which) {
        switch (type) {
            case VOLUME: {
                VolumetricEnergyDensityUnit unit = VolumetricEnergyDensityUnit.values()[which];
                model.setPreferredVolumetricUnit(unit);
                break;
            }
            case MASS: {
                GravimetricEnergyDensityUnit unit = GravimetricEnergyDensityUnit.values()[which];
                model.setPreferredGravimetricUnit(unit);
                break;
            }
            default: throw new IllegalArgumentException();
        }
    }

    @Override
    public void onSolidUnitSettingsClicked() {
        onUnitSettingsClicked(AmountUnitType.MASS);
    }

    @Override
    public void showCurrentUnits() {
        view.setLiquidUnit(model.getUnitName(model.getPreferredVolumetricUnit()));
        view.setSolidUnit(model.getUnitName(model.getPreferredGravimetricUnit()));
    }

    @Override
    public void onStart() {
        Subscription subscription= model.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SettingsChangedEvent>() {
            @Override
            public void call(SettingsChangedEvent event) {
                if (event instanceof EnergyUnit.Mass)
                    view.setSolidUnit(model.getUnitName(((EnergyUnit.Mass) event).getValue()));
                else if (event instanceof EnergyUnit.Volume)
                    view.setLiquidUnit(model.getUnitName(((EnergyUnit.Volume) event).getValue()));

            }
        });
        subscriptions.add(subscription);
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }
}
