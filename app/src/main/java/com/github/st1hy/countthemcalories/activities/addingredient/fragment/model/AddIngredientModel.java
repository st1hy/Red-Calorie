package com.github.st1hy.countthemcalories.activities.addingredient.fragment.model;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.EditIngredientActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.withpicture.model.WithPictureModel;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class AddIngredientModel extends WithPictureModel {
    final SettingsModel settingsModel;
    final IngredientTagsModel tagsModel;
    final RxIngredientsDatabaseModel databaseModel;
    final Resources resources;
    final Intent intent;
    final ParcelableProxy parcelableProxy;

    EnergyUnit energyUnit;
    AmountUnit amountUnit;

    final Observable<Void> loading;

    IngredientTypeParcel source;
    String name;
    String energyValue;
    AmountUnitType amountType;
    Uri imageUri;

    @Inject
    public AddIngredientModel(@NonNull SettingsModel settingsModel,
                              @NonNull IngredientTagsModel tagsModel,
                              @NonNull RxIngredientsDatabaseModel databaseModel,
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
            this.source = parcelableProxy.source;
            this.name = parcelableProxy.name;
            this.energyValue = parcelableProxy.energyValue;
            this.amountType = parcelableProxy.amountType;
            this.imageUri = parcelableProxy.imageUri;
            this.loading = Observable.just(null);
            energyUnit = settingsModel.getEnergyUnit();
            amountUnit = settingsModel.getAmountUnitFrom(amountType);
        } else {
            parcelableProxy = new ParcelableProxy();
            source = null;
            if (intent != null) {
                if (EditIngredientActivity.ACTION_EDIT.equals(intent.getAction())) {
                    source = intent.getParcelableExtra(EditIngredientActivity.EXTRA_EDIT_INGREDIENT_PARCEL);
                }
            }
            if (source != null) {
                this.loading = databaseModel.unParcel(source)
                        .map(onLoadedFromDatabase())
                        .replay().autoConnect();
                loading.subscribe();
            } else {
                this.source = null;
                this.name = "";
                this.energyValue = "";
                this.amountType = getUnitTypeFrom(intent);
                this.imageUri = Uri.EMPTY;
                this.loading = Observable.just(null);
                energyUnit = settingsModel.getEnergyUnit();
                amountUnit = settingsModel.getAmountUnitFrom(amountType);
            }
        }
        this.parcelableProxy = parcelableProxy;
    }

    @NonNull
    AmountUnitType getUnitTypeFrom(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (AddIngredientType.DRINK.getAction().equals(action)) {
                return AmountUnitType.VOLUME;
            } else if (AddIngredientType.MEAL.getAction().equals(action)) {
                return AmountUnitType.MASS;
            }
        }
        return AmountUnitType.MASS;
    }

    /**
     * Notifies that loading of this model has ended.
     */
    @NonNull
    public Observable<Void> getLoading() {
        return loading;
    }

    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(ParcelableProxy.STATE_MODEL, parcelableProxy.snapshot(this));
    }

    @NonNull
    public String getEnergyDensityUnit() {
        String energy = settingsModel.getUnitName(energyUnit);
        String amount = settingsModel.getUnitName(amountUnit);
        return resources.getString(R.string.format_value_fraction, "", energy, amount).trim();
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

    /**
     * @return observable ingredient that was added or updated in database OR {@link IngredientTypeCreateException}
     * if not data is in incorrect state to be saved
     */
    @NonNull
    public Observable<IngredientTemplate> saveIntoDatabase() {
        List<IngredientTypeCreateException.ErrorType> errorList = canCreateIngredient();
        if (errorList.isEmpty()) {
            if (isEditing()) {
                return updateExistingInDatabase();
            } else {
                return insertNewIntoDatabase();
            }
        } else {
            return Observable.error(new IngredientTypeCreateException(errorList));
        }
    }

    @NonNull
    private Observable<IngredientTemplate> insertNewIntoDatabase() {
        IngredientTemplate template = new IngredientTemplate();
        setIntoTemplate(template);
        return databaseModel.addNew(template, tagsModel.getTagIds());
    }

    @NonNull
    private Observable<IngredientTemplate> updateExistingInDatabase() {
        return databaseModel.unParcel(source)
                .map(new Func1<IngredientTemplate, IngredientTemplate>() {
                    @Override
                    public IngredientTemplate call(IngredientTemplate template) {
                        setIntoTemplate(template);
                        return template;
                    }
                }).flatMap(new Func1<IngredientTemplate, Observable<IngredientTemplate>>() {
                    @Override
                    public Observable<IngredientTemplate> call(IngredientTemplate ingredientTemplate) {
                        return databaseModel.update(ingredientTemplate, tagsModel.getTagIds());
                    }
                });
    }

    private void setIntoTemplate(@NonNull IngredientTemplate template) {
        template.setName(getName());
        template.setImageUri(getImageUri());
        if (template.getCreationDate() == null)
            template.setCreationDate(DateTime.now());
        template.setAmountType(amountType);
        template.setEnergyDensityAmount(getEnergyUnit().convertToDatabaseFormat().getValue());
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
    List<IngredientTypeCreateException.ErrorType> canCreateIngredient() {
        return canCreateIngredient(name, energyValue);
    }

    public List<IngredientTypeCreateException.ErrorType> canCreateIngredient(@NonNull String name, @NonNull String value) {
        List<IngredientTypeCreateException.ErrorType> errors = new ArrayList<>(4);
        if (isEmpty(name)) errors.add(IngredientTypeCreateException.ErrorType.NO_NAME);
        if (isEmpty(value)) errors.add(IngredientTypeCreateException.ErrorType.NO_VALUE);
        else if (!isValueGreaterThanZero(value)) errors.add(IngredientTypeCreateException.ErrorType.ZERO_VALUE);
        return errors;
    }

    static boolean isEmpty(@NonNull String name) {
        return name.trim().isEmpty();
    }

    boolean isValueGreaterThanZero(@NonNull String value) {
        return getEnergyUnit(value).getValue().compareTo(BigDecimal.ZERO) > 0;
    }

    @NonNull
    private Func1<IngredientTemplate, Void> onLoadedFromDatabase() {
        return new Func1<IngredientTemplate, Void>() {
            @Override
            public Void call(IngredientTemplate ingredientTemplate) {
                name = ingredientTemplate.getName();
                imageUri = ingredientTemplate.getImageUri();
                amountType = ingredientTemplate.getAmountType();

                energyUnit = settingsModel.getEnergyUnit();
                amountUnit = settingsModel.getAmountUnitFrom(amountType);
                energyValue = EnergyDensity.fromDatabaseValue(amountType, ingredientTemplate.getEnergyDensityAmount())
                        .convertTo(energyUnit, amountUnit)
                        .getValue()
                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                        .stripTrailingZeros()
                        .toPlainString();
                tagsModel.replaceTags(Collections2.transform(ingredientTemplate.getTags(), intoTags()));
                return null;
            }
        };
    }

    @NonNull
    private static Function<JointIngredientTag, Tag> intoTags() {
        return new Function<JointIngredientTag, Tag>() {
            @Nullable
            @Override
            public Tag apply(JointIngredientTag input) {
                return input.getTag();
            }
        };
    }

    public boolean isEditing() {
        return source != null;
    }

    static class ParcelableProxy implements Parcelable {
        static String STATE_MODEL = "add ingredient model";
        static final int FLAG_HAS_SOURCE = 0x01;
        String name;
        String energyValue;
        Uri imageUri;
        AmountUnitType amountType;
        IngredientTypeParcel source;

        ParcelableProxy() {
        }

        ParcelableProxy snapshot(@NonNull AddIngredientModel model) {
            this.source = model.source;
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
                int flags = source.readInt();
                if ((flags & FLAG_HAS_SOURCE) > 0)
                    parcelableProxy.source = source.readParcelable(getClass().getClassLoader());
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
            boolean hasSource = source != null;
            dest.writeInt(hasSource ? FLAG_HAS_SOURCE : 0);
            if (hasSource)
                dest.writeParcelable(source, 0);
            dest.writeString(name);
            dest.writeString(energyValue);
            dest.writeString(amountType.toString());
            dest.writeParcelable(imageUri, flags);
        }
    }

    public static class IngredientTypeCreateException extends IllegalStateException {
        private final List<ErrorType> errors;

        public IngredientTypeCreateException(@NonNull List<ErrorType> errors) {
            this.errors = errors;
        }

        @NonNull
        public List<ErrorType> getErrors() {
            return errors;
        }

        public enum ErrorType {
            NO_NAME(R.string.add_ingredient_name_error_empty),
            NO_VALUE(R.string.add_ingredient_energy_density_error_empty),
            ZERO_VALUE(R.string.add_ingredient_energy_density_error_zero);

            private final int errorResId;

            ErrorType(@StringRes int errorResId) {
                this.errorResId = errorResId;
            }

            @StringRes
            public int getErrorResId() {
                return errorResId;
            }

        }
    }
}
