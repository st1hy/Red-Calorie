package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.withpicture.model.WithPictureModel;

import javax.inject.Inject;

public class AddMealModel extends WithPictureModel {
    final MealIngredientsListModel ingredientsListModel;
    final ParcelableProxy parcelableProxy;

    String name;
    Uri imageUri;

    @Inject
    public AddMealModel(@NonNull MealIngredientsListModel ingredientsListModel,
                        @Nullable Bundle savedState) {
        this.ingredientsListModel = ingredientsListModel;

        ParcelableProxy parcelableProxy = null;
        if (savedState != null) {
            parcelableProxy = savedState.getParcelable(ParcelableProxy.STATE_MODEL);
        }
        if (parcelableProxy != null) {
            this.name = parcelableProxy.name;
            this.imageUri = parcelableProxy.imageUri;
        } else {
            this.name = "";
            this.imageUri = Uri.EMPTY;
            parcelableProxy = new ParcelableProxy();
        }
        this.parcelableProxy = parcelableProxy;
    }

    @Override
    public int getImageSourceDialogTitleResId() {
        return R.string.add_meal_image_select_title;
    }

    @Override
    public int getImageSourceOptionArrayResId() {
        return R.array.add_meal_image_select_options;
    }

    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(ParcelableProxy.STATE_MODEL, parcelableProxy.snapshot(this));
        ingredientsListModel.onSaveState(outState);
    }

    public @NonNull String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(@NonNull Uri imageUri) {
        this.imageUri = imageUri;
    }

    static class ParcelableProxy implements Parcelable {
        static String STATE_MODEL = "add meal model";
        String name;
        Uri imageUri;

        ParcelableProxy() {
        }

        ParcelableProxy snapshot(@NonNull AddMealModel model) {
            this.name = model.name;
            this.imageUri = model.imageUri;
            return this;
        }

        public static final Creator<ParcelableProxy> CREATOR = new Creator<ParcelableProxy>() {
            @Override
            public ParcelableProxy createFromParcel(Parcel source) {
                ParcelableProxy parcelableProxy = new ParcelableProxy();
                parcelableProxy.name = source.readString();
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
            dest.writeParcelable(imageUri, flags);
        }
    }
}
