package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.rx.ObservableValue;
import com.github.st1hy.countthemcalories.core.ui.withpicture.model.WithPictureModel;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class AddIngredientModel extends WithPictureModel {
    final SettingsModel settingsModel;

    final ObservableValue<EnergyDensityUnit> unit;
    ParcelableProxy parcelableProxy;

    @Inject
    public AddIngredientModel(@NonNull SettingsModel settingsModel, @Nullable Bundle savedState) {
        this.settingsModel = settingsModel;
        if (savedState != null) {
            parcelableProxy = savedState.getParcelable(ParcelableProxy.MODEL_STATE);
        }
        if (parcelableProxy != null) {
            this.unit = new ObservableValue<>(parcelableProxy.unit);
        } else {
            EnergyDensityUnit defaultUnit = settingsModel.getPreferredGravimetricUnit();
            this.unit = new ObservableValue<>(defaultUnit);
            parcelableProxy = new ParcelableProxy(this);
        }
    }

    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(ParcelableProxy.MODEL_STATE, parcelableProxy.snapshot(this));
    }

    public void setUnit(@NonNull EnergyDensityUnit unit) {
        this.unit.setValue(unit);
    }

    @NonNull
    public Observable<EnergyDensityUnit> getUnitObservable() {
        return unit.asObservable();
    }

    @NonNull
    public EnergyDensityUnit[] getUnitSelection() {
        return new EnergyDensityUnit[]{
                settingsModel.getPreferredGravimetricUnit(),
                settingsModel.getPreferredVolumetricUnit()
        };
    }

    @NonNull
    public String[] getUnitSelectionOptions() {
        EnergyDensityUnit[] unitSelection = getUnitSelection();
        String[] options = new String[unitSelection.length];
        for (int i = 0; i < unitSelection.length; i++) {
            options[i] = settingsModel.getUnitName(unitSelection[i]);
        }
        return options;
    }

    @NonNull
    public Func1<EnergyDensityUnit, String> unitAsString() {
        return new Func1<EnergyDensityUnit, String>() {
            @Override
            public String call(EnergyDensityUnit energyDensityUnit) {
                return settingsModel.getUnitName(energyDensityUnit);
            }
        };
    }

    @Override
    public int getImageSourceDialogTitleResId() {
        return R.string.add_ingredient_image_select_title;
    }

    @Override
    public int getImageSourceOptionArrayResId() {
        return R.array.add_ingredient_image_select_options;
    }

    @StringRes
    public int getSelectUnitDialogTitle() {
        return R.string.add_ingredient_select_unit_dialog_title;
    }


    static class ParcelableProxy implements Parcelable {
        static String MODEL_STATE = "add ingredient model";
        EnergyDensityUnit unit;

        ParcelableProxy(@NonNull AddIngredientModel model) {
            snapshot(model);
        }

        ParcelableProxy snapshot(@NonNull AddIngredientModel model) {
            this.unit = model.unit.getValue();
            return this;
        }

        ParcelableProxy() {
        }

        public static final Creator<ParcelableProxy> CREATOR = new Creator<ParcelableProxy>() {
            @Override
            public ParcelableProxy createFromParcel(Parcel source) {
                ParcelableProxy parcelableProxy = new ParcelableProxy();
                parcelableProxy.unit = EnergyDensityUtils.fromString(source.readString());
                return parcelableProxy;
            }

            @Override
            public ParcelableProxy[] newArray(int size) {
                return new ParcelableProxy[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(EnergyDensityUtils.getString(unit));
        }
    }
}
