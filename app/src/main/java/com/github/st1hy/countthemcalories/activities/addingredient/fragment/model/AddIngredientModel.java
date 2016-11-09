package com.github.st1hy.countthemcalories.activities.addingredient.fragment.model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.picture.PictureModel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

import org.joda.time.DateTime;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class AddIngredientModel implements PictureModel {

    public static final String SAVED_INGREDIENT_MODEL = "add ingredient model";

    @Nullable
    private Long id;
    @NonNull
    private AmountUnitType amountType;
    @NonNull
    private String name;
    @NonNull
    private String energyValue;
    @NonNull
    private Uri imageUri;
    @Nullable
    private DateTime creationDate;

    @ParcelConstructor
    public AddIngredientModel(@NonNull String name,
                              @NonNull AmountUnitType amountType,
                              @NonNull String energyValue,
                              @NonNull Uri imageUri,
                              @Nullable DateTime creationDate,
                              @Nullable Long id) {
        this.amountType = amountType;
        this.name = name;
        this.energyValue = energyValue;
        this.imageUri = imageUri;
        this.creationDate = creationDate;
        this.id = id;
    }

    @Override
    public int getImageSourceDialogTitleResId() {
        return R.string.add_ingredient_image_select_title;
    }

    @Override
    public int getSelectImageSourceOptions() {
        return R.array.add_ingredient_image_select_options;
    }

    @Override
    public int getSelectImageSourceAndRemoveOptions() {
        return R.array.add_ingredient_image_select_remove_options;
    }

    @Override
    public boolean hasImage() {
        return !Uri.EMPTY.equals(imageUri);
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

    public void setAmountType(@NonNull AmountUnitType amountType) {
        this.amountType = amountType;
    }

    @NonNull
    public AmountUnitType getAmountType() {
        return amountType;
    }

    @StringRes
    public int getSelectTypeDialogTitle() {
        return R.string.add_ingredient_select_type_title;
    }

    @Nullable
    public DateTime getCreationDate() {
        return creationDate;
    }

    public Long getId() {
        return id;
    }
}
