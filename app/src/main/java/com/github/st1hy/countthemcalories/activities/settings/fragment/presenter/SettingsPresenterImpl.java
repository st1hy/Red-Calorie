package com.github.st1hy.countthemcalories.activities.settings.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SelectUnitViewHolder;
import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SettingsView;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingUnit;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsChangedEvent;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.activities.settings.model.UnitChangedEvent;
import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.database.unit.Unit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class SettingsPresenterImpl implements SettingsPresenter {

    @NonNull
    private final SettingsView view;
    @NonNull
    private final SettingsModel model;
    @NonNull
    private final DialogView dialogView;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public SettingsPresenterImpl(@NonNull SettingsView view,
                                 @NonNull SettingsModel model,
                                 @NonNull DialogView dialogView) {
        this.view = view;
        this.model = model;
        this.dialogView = dialogView;
    }

    @Override
    public void onStart() {
        subscriptions.add(model.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onUnitChanged()));
        setupView();
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    private void setupView() {
        for (SettingUnit setting : SettingUnit.values()) {
            SelectUnitViewHolder viewHolder = getViewHolder(setting);
            subscriptions.add(viewHolder.clickObservable()
                    .flatMap(selectUnitDialog(setting))
                    .subscribe(onUnitSelected(setting)));
            viewHolder.setTitle(setting.getTitleRes());
            viewHolder.setUnit(model.getUnitName(setting.getUnitFrom(model)));
        }
    }

    @NonNull
    private Action1<SettingsChangedEvent> onUnitChanged() {
        return new Action1<SettingsChangedEvent>() {
            @Override
            public void call(SettingsChangedEvent event) {
                if (event instanceof UnitChangedEvent) {
                    onUnitChanged((UnitChangedEvent) event);
                }
            }
        };
    }

    private void onUnitChanged(@NonNull UnitChangedEvent event) {
        SettingUnit setting = event.getSetting();
        getViewHolder(setting).setUnit(model.getUnitName(event.getUnit()));
    }

    @NonNull
    private Func1<Void, Observable<Integer>> selectUnitDialog(@NonNull final SettingUnit setting) {
        return new Func1<Void, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Void aVoid) {
                Unit[] units = setting.options();
                String[] values = new String[units.length];
                for (int i = 0; i < units.length; i++) {
                    values[i] = model.getUnitName(units[i]);
                }
                return dialogView.showAlertDialog(model.getPreferredUnitDialogTitle(), values);
            }
        };
    }

    @NonNull
    private Action1<Integer> onUnitSelected(@NonNull final SettingUnit setting) {
        return new Action1<Integer>() {
            @Override
            public void call(Integer which) {
                setting.setUnitTo(model, which);
            }
        };
    }

    @NonNull
    private SelectUnitViewHolder getViewHolder(@NonNull SettingUnit settingUnit) {
        switch (settingUnit) {
            case ENERGY:
                return view.getEnergyHolder();
            case MASS:
                return view.getMassHolder();
            case VOLUME:
                return view.getVolumeHolder();
            default:
                throw new UnsupportedOperationException();
        }
    }
}
