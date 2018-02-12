package com.github.st1hy.countthemcalories.ui.activities.settings.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.settings.model.SettingUnit;
import com.github.st1hy.countthemcalories.ui.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.ui.activities.settings.model.UnitChangedEvent;
import com.github.st1hy.countthemcalories.ui.activities.settings.model.unit.NamedUnit;
import com.github.st1hy.countthemcalories.ui.activities.settings.view.SelectUnitViewHolder;
import com.github.st1hy.countthemcalories.ui.activities.settings.view.SettingsView;
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
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
                .subscribe(event -> {
                    if (event instanceof UnitChangedEvent) {
                        onUnitChanged((UnitChangedEvent) event);
                    }
                }));
        setupView();
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    private void setupView() {
        for (SettingUnit setting : SettingUnit.values()) {
            SelectUnitViewHolder viewHolder = getViewHolder(setting);
            subscriptions.add(
                    viewHolder.clickObservable()
                            .map(v -> getUnitDialogOptions(setting))
                            .flatMap(values ->
                                    dialogView.showAlertDialog(
                                            model.getPreferredUnitDialogTitle(), values)
                            )
                            .subscribe(selected -> setting.setUnitTo(model, selected))
            );
            viewHolder.setTitle(setting.getTitleRes());
            viewHolder.setUnit(model.getUnitName(setting.getUnitFrom(model)));
        }
    }

    @NonNull
    private String[] getUnitDialogOptions(@NonNull SettingUnit setting) {
        NamedUnit[] units = setting.options();
        String[] values = new String[units.length];
        for (int i = 0; i < units.length; i++) {
            values[i] = model.getUnitName(units[i]);
        }
        return values;
    }

    private void onUnitChanged(@NonNull UnitChangedEvent event) {
        SettingUnit setting = event.getSetting();
        getViewHolder(setting).setUnit(model.getUnitName(event.getUnit()));
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
            case BODY_MASS:
                return view.getBodyMassHolder();
            default:
                throw new UnsupportedOperationException();
        }
    }
}
