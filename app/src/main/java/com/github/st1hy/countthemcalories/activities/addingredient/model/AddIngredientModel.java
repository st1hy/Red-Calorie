package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.rx.ObservableValue;
import com.github.st1hy.countthemcalories.core.ui.withpicture.model.WithPictureModel;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;

import org.joda.time.DateTime;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class AddIngredientModel extends WithPictureModel {
    final SettingsModel settingsModel;
    final IngredientTagsModel tagsModel;
    final IngredientTypesModel databaseModel;
    ParcelableProxy parcelableProxy;

    String name;
    String energyValue;
    final ObservableValue<EnergyDensityUnit> unit;
    Uri imageUri;

    @Inject
    public AddIngredientModel(@NonNull SettingsModel settingsModel,
                              @NonNull IngredientTagsModel tagsModel,
                              @NonNull IngredientTypesModel databaseModel,
                              @Nullable Bundle savedState) {
        this.settingsModel = settingsModel;
        this.tagsModel = tagsModel;
        this.databaseModel = databaseModel;
        if (savedState != null) {
            parcelableProxy = savedState.getParcelable(ParcelableProxy.STATE_MODEL);
        }
        if (parcelableProxy != null) {
            this.unit = new ObservableValue<>(parcelableProxy.unit);
            this.name = parcelableProxy.name;
            this.energyValue = parcelableProxy.energyValue;
            this.imageUri = parcelableProxy.imageUri;
        } else {
            EnergyDensityUnit defaultUnit = settingsModel.getPreferredGravimetricUnit();
            this.name = "";
            this.energyValue = "";
            this.unit = new ObservableValue<>(defaultUnit);
            this.imageUri = Uri.EMPTY;
            parcelableProxy = new ParcelableProxy();
        }
    }

    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(ParcelableProxy.STATE_MODEL, parcelableProxy.snapshot(this));
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

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setEnergyValue(@NonNull String energyValue) {
        this.energyValue = energyValue;
    }

    @NonNull
    public String getEnergyValue() {
        return energyValue;
    }

    public void setImageUri(@NonNull Uri imageUri) {
        this.imageUri = imageUri;
    }

    @NonNull
    public Uri getImageUri() {
        return imageUri;
    }

    @NonNull
    public Observable<Void> insertIntoDatabase() {
        IngredientTemplate template = new IngredientTemplate(null, getName(), getImageUri(),
                DateTime.now(), getEnergyUnit());
        return databaseModel.addNewAndRefresh(template, tagsModel.getAll())
                .map(intoVoidCall());
    }

    @NonNull
    public EnergyDensity getEnergyUnit() {
        return EnergyDensityUtils.getOrZero(unit.getValue(), getEnergyValue());
    }

    public boolean canCreateIngredient() {
        return !getName().trim().isEmpty() &&
                !getEnergyValue().trim().isEmpty() &&
                getEnergyUnit().getValue().compareTo(BigDecimal.ZERO) > 0;
    }

    static class ParcelableProxy implements Parcelable {
        static String STATE_MODEL = "add ingredient model";
        EnergyDensityUnit unit;
        String name;
        String energyValue;
        Uri imageUri;

        ParcelableProxy() {
        }

        ParcelableProxy snapshot(@NonNull AddIngredientModel model) {
            this.unit = model.unit.getValue();
            this.name = model.name;
            this.energyValue = model.energyValue;
            this.imageUri = model.imageUri;
            return this;
        }

        public static final Creator<ParcelableProxy> CREATOR = new Creator<ParcelableProxy>() {
            @Override
            public ParcelableProxy createFromParcel(Parcel source) {
                ParcelableProxy parcelableProxy = new ParcelableProxy();
                parcelableProxy.name = source.readString();
                parcelableProxy.energyValue = source.readString();
                parcelableProxy.unit = EnergyDensityUtils.fromString(source.readString());
                parcelableProxy.imageUri = source.readParcelable(Uri.class.getClassLoader());
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
            dest.writeString(name);
            dest.writeString(energyValue);
            dest.writeString(EnergyDensityUtils.getString(unit));
            dest.writeParcelable(imageUri, flags);
        }
    }

    @NonNull
    private Func1<Cursor, Void> intoVoidCall() {
        return new Func1<Cursor, Void>() {
            @Override
            public Void call(Cursor cursor) {
                return null;
            }
        };
    }
}
