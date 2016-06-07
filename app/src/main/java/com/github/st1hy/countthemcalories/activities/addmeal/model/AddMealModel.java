package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDatabaseModel;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.withpicture.model.WithPictureModel;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;

import org.joda.time.DateTime;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

public class AddMealModel extends WithPictureModel {
    final MealIngredientsListModel ingredientsListModel;
    final ParcelableProxy parcelableProxy;
    final MealDatabaseModel databaseModel;
    final Resources resources;

    long mealId;
    String name;
    Uri imageUri;
    final Observable<Void> loading;

    @Inject
    public AddMealModel(@NonNull MealIngredientsListModel ingredientsListModel,
                        @NonNull MealDatabaseModel databaseModel,
                        @NonNull Resources resources,
                        @Nullable Intent intent,
                        @Nullable Bundle savedState) {
        this.ingredientsListModel = ingredientsListModel;
        this.databaseModel = databaseModel;
        this.resources = resources;

        ParcelableProxy parcelableProxy = null;
        if (savedState != null) {
            parcelableProxy = savedState.getParcelable(ParcelableProxy.STATE_MODEL);
        }
        if (parcelableProxy != null) {
            this.mealId = parcelableProxy.mealId;
            this.name = parcelableProxy.name;
            this.imageUri = parcelableProxy.imageUri;
            loading = Observable.just(null);
        } else {
            Observable<Void> observable = null;
            parcelableProxy = new ParcelableProxy();
            this.mealId = -1L;
            this.name = "";
            this.imageUri = Uri.EMPTY;
            if (intent != null) {
                MealParcel parcel = intent.getParcelableExtra(AddMealActivity.EXTRA_MEAL_PARCEL);
                if (parcel != null) {
                    observable = loadParcel(parcel);
                }
            }
            if (observable == null) observable = Observable.just(null);
            loading = observable;
        }
        this.parcelableProxy = parcelableProxy;
    }

    Observable<Void> loadParcel(@NonNull MealParcel parcel) {
        Observable<Meal> observable = databaseModel.unParcel(parcel).replay().autoConnect().share();
        observable.subscribe(new Action1<Meal>() {
            @Override
            public void call(Meal meal) {
                mealId = meal.getId();
                name = meal.getName();
                imageUri = meal.getImageUri();
            }
        });
        return observable.map(Functions.INTO_VOID);
    }

    @NonNull
    public Observable<Void> getLoading() {
        return loading;
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

    public
    @NonNull
    String getName() {
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

    @NonNull
    public Observable<Void> saveToDatabase() {
        Meal meal = new Meal();
        if (mealId != -1L) meal.setId(mealId);
        meal.setName(getName());
        if (meal.getCreationDate() == null)
            meal.setCreationDate(DateTime.now());
        meal.setImageUri(getImageUri());
        return databaseModel.insertOrUpdate(meal, ingredientsListModel.getIngredients());
    }

    @Nullable
    public String getNameError() {
        return getName().trim().isEmpty() ?
                resources.getString(R.string.add_meal_name_empty_error) : null;
    }

    @Nullable
    public String getIngredientsError() {
        if (ingredientsListModel.getItemsCount() == 0) {
            return resources.getString(R.string.add_meal_ingredients_empty_error);
        } else return null;
    }

    static class ParcelableProxy implements Parcelable {
        static String STATE_MODEL = "add meal model";
        long mealId;
        String name;
        Uri imageUri;

        ParcelableProxy() {
        }

        ParcelableProxy snapshot(@NonNull AddMealModel model) {
            this.mealId = model.mealId;
            this.name = model.name;
            this.imageUri = model.imageUri;
            return this;
        }

        public static final Creator<ParcelableProxy> CREATOR = new Creator<ParcelableProxy>() {
            @Override
            public ParcelableProxy createFromParcel(Parcel source) {
                ParcelableProxy parcelableProxy = new ParcelableProxy();
                parcelableProxy.mealId = source.readLong();
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
            dest.writeLong(mealId);
            dest.writeString(name);
            dest.writeParcelable(imageUri, flags);
        }
    }
}
