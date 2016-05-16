package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.withpicture.model.WithPictureModel;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class AddIngredientModel extends WithPictureModel {
    final SettingsModel settingsModel;
    final IngredientTagsModel tagsModel;
    final IngredientTypesDatabaseModel databaseModel;
    final Resources resources;
    final Intent intent;
    final ParcelableProxy parcelableProxy;

    final EnergyUnit energyUnit;
    final AmountUnit amountUnit;

    String name;
    String energyValue;
    AmountUnitType amountType;
    Uri imageUri;

    @Inject
    public AddIngredientModel(@NonNull SettingsModel settingsModel,
                              @NonNull IngredientTagsModel tagsModel,
                              @NonNull IngredientTypesDatabaseModel databaseModel,
                              @NonNull Resources resources,
                              @Nullable Bundle savedState,
                              @Nullable Intent intent) {
        this.settingsModel = settingsModel;
        this.tagsModel = tagsModel;
        this.databaseModel = databaseModel;
        this.resources = resources;
        this.intent = intent;
        ParcelableProxy parcelableProxy = null;
        if (savedState != null) {
            parcelableProxy = savedState.getParcelable(ParcelableProxy.STATE_MODEL);
        }
        if (parcelableProxy != null) {
            this.name = parcelableProxy.name;
            this.energyValue = parcelableProxy.energyValue;
            this.amountType = parcelableProxy.amountType;
            this.imageUri = parcelableProxy.imageUri;
        } else {
            this.name = "";
            this.energyValue = "";
            this.amountType = getUnitTypeFrom(intent);
            this.imageUri = Uri.EMPTY;
            parcelableProxy = new ParcelableProxy();
        }
        this.parcelableProxy = parcelableProxy;
        energyUnit = settingsModel.getEnergyUnit();
        amountUnit = settingsModel.getAmountUnitFrom(amountType);
    }

    @NonNull
    private AmountUnitType getUnitTypeFrom(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case AddIngredientActivity.ACTION_CREATE_DRINK:
                        return AmountUnitType.VOLUME;
                    case AddIngredientActivity.ACTION_CREATE_MEAL:
                        return AmountUnitType.MASS;
                }
            }
        }
        return AmountUnitType.MASS;
    }

    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(ParcelableProxy.STATE_MODEL, parcelableProxy.snapshot(this));
    }

    @NonNull
    public String getEnergyDensityUnit() {
        return resources.getString(R.string.format_value_fraction, "",
                settingsModel.getUnitName(energyUnit), settingsModel.getUnitName(amountUnit))
                .trim();
    }

    @Override
    public int getImageSourceDialogTitleResId() {
        return R.string.add_ingredient_image_select_title;
    }

    @Override
    public int getImageSourceOptionArrayResId() {
        return R.array.add_ingredient_image_select_options;
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
    public Observable<List<IngredientTypeCreateError>> insertIntoDatabase() {
        List<IngredientTypeCreateError> errorList = canCreateIngredient();
        if (errorList.isEmpty()) {
            IngredientTemplate template = new IngredientTemplate();
            template.setName(getName());
            template.setImageUri(getImageUri());
            template.setCreationDate(DateTime.now());
            template.setAmountType(amountType);
            template.setEnergyDensityAmount(getEnergyUnit().convertToDatabaseFormat().getValue());
            return databaseModel.addNewAndRefresh(template, tagsModel.getTagIds())
                    .map(intoNoError());
        } else{
            return Observable.just(errorList);
        }
    }

    @NonNull
    EnergyDensity getEnergyUnit() {
        return getEnergyUnit(getEnergyValue());
    }

    @NonNull
    EnergyDensity getEnergyUnit(@NonNull String value) {
        return EnergyDensityUtils.getOrZero(energyUnit, amountUnit, value);
    }

    @NonNull
    List<IngredientTypeCreateError> canCreateIngredient() {
        return canCreateIngredient(name, energyValue);
    }

    public List<IngredientTypeCreateError> canCreateIngredient(@NonNull String name, @NonNull String value) {
        List<IngredientTypeCreateError> errors = new ArrayList<>(4);
        if (isEmpty(name)) errors.add(IngredientTypeCreateError.NO_NAME);
        if (isEmpty(value)) errors.add(IngredientTypeCreateError.NO_VALUE);
        else if (!isValueGreaterThanZero(value)) errors.add(IngredientTypeCreateError.ZERO_VALUE);
        return errors;
    }

    static boolean isEmpty(@NonNull String name) {
        return name.trim().isEmpty();
    }

    boolean isValueGreaterThanZero(@NonNull String value) {
        return getEnergyUnit(value).getValue().compareTo(BigDecimal.ZERO) > 0;
    }

    @NonNull
    private Func1<Cursor, List<IngredientTypeCreateError>> intoNoError() {
        return new Func1<Cursor, List<IngredientTypeCreateError>>() {
            @Override
            public List<IngredientTypeCreateError> call(Cursor cursor) {
                return Collections.emptyList();
            }
        };
    }

    public enum IngredientTypeCreateError {
        NO_NAME(R.string.add_ingredient_name_error_empty),
        NO_VALUE(R.string.add_ingredient_energy_density_error_empty),
        ZERO_VALUE(R.string.add_ingredient_energy_density_error_zero);

        private final int errorResId;

        IngredientTypeCreateError(@StringRes int errorResId) {
            this.errorResId = errorResId;
        }

        @StringRes
        public int getErrorResId() {
            return errorResId;
        }

    }

    static class ParcelableProxy implements Parcelable {
        static String STATE_MODEL = "add ingredient model";
        String name;
        String energyValue;
        Uri imageUri;
        AmountUnitType amountType;

        ParcelableProxy() {
        }

        ParcelableProxy snapshot(@NonNull AddIngredientModel model) {
            this.amountType = model.amountType;
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
                parcelableProxy.amountType = AmountUnitType.valueOf(source.readString());
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
            dest.writeString(amountType.toString());
            dest.writeParcelable(imageUri, flags);
        }
    }
}
