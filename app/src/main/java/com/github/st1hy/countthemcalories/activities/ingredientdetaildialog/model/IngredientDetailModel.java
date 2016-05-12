package com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.model;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.property.BigDecimalPropertyConverter;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.google.common.base.Optional;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view.IngredientDetailsActivity.ACTION_EDIT_INGREDIENT;
import static com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view.IngredientDetailsActivity.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL;
import static com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view.IngredientDetailsActivity.EXTRA_INGREDIENT_TEMPLATE_PARCEL;

public class IngredientDetailModel {
    final IngredientTypesDatabaseModel ingredientTypesModel;
    final ParcelableProxy parcelableProxy;
    final Resources resources;

    final boolean isDataValid;
    private final Ingredient ingredient;
    private final Observable<Ingredient> ingredientObservable;

    @Inject
    public IngredientDetailModel(@NonNull IngredientTypesDatabaseModel ingredientTypesModel,
                                 @NonNull Resources resources,
                                 @Nullable Intent intent,
                                 @Nullable Bundle savedState) {
        this.ingredientTypesModel = ingredientTypesModel;
        this.resources = resources;
        ParcelableProxy parcelableProxy = null;
        if (savedState != null) {
            parcelableProxy = savedState.getParcelable(ParcelableProxy.STATE_MODEL);
        }
        if (parcelableProxy != null) {
            isDataValid = true;
            ingredient = parcelableProxy.ingredient;
            if (parcelableProxy.isReCreated) {
                ingredientObservable = loadItem(ingredient);
            } else {
                ingredientObservable = Observable.just(ingredient);
            }
        } else {
            parcelableProxy = new ParcelableProxy();
            isDataValid = isIntentValid(intent);
            if (isDataValid) {
                IngredientTypeParcel typeParcel = intent.getParcelableExtra(EXTRA_INGREDIENT_TEMPLATE_PARCEL);
                String valueAsString = intent.getStringExtra(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL);
                ingredient = new Ingredient();
                ingredient.setAmount(new BigDecimal(valueAsString));
                ingredientObservable = loadFromParcel(typeParcel);
            } else {
                Timber.e("Incorrect starting intent: %s", intent);
                ingredient = null;
                ingredientObservable = Observable.empty();
            }
        }
        this.parcelableProxy = parcelableProxy;
    }

    /**
     * not null and valid only when #isDataValid is true, after #getIngredientObservable returns any value
     */
    @NonNull
    public Ingredient getIngredient() {
        return ingredient;
    }

    public boolean isDataValid() {
        return isDataValid;
    }

    public Observable<Ingredient> getIngredientObservable() {
        return ingredientObservable;
    }

    public void onSaveState(@NonNull Bundle savedState) {
        savedState.putParcelable(ParcelableProxy.STATE_MODEL, parcelableProxy.snapshot(this));
    }

    @NonNull
    private Observable<Ingredient> loadItem(@NonNull Ingredient ingredient) {
        Observable<Ingredient> observable = ingredientTypesModel.getById(ingredient.getIngredientTypeId())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<IngredientTemplate, Ingredient>() {
                    @Override
                    public Ingredient call(IngredientTemplate template) {
                        IngredientDetailModel.this.ingredient.setIngredientType(template);
                        return IngredientDetailModel.this.ingredient;
                    }
                })
                .replay().autoConnect().share();
        observable.subscribe(new IngredientLoader());
        return observable;
    }

    private Observable<Ingredient> loadFromParcel(@NonNull IngredientTypeParcel typeParcel) {
        Observable<Ingredient> observable =  ingredientTypesModel.unParcel(typeParcel)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<IngredientTemplate, Ingredient>() {
                    @Override
                    public Ingredient call(IngredientTemplate template) {
                        ingredient.setIngredientType(template);
                        return ingredient;
                    }
                }).replay().autoConnect().share();
        observable.subscribe(new IngredientLoader());
        return observable;
    }

    private boolean isIntentValid(@Nullable Intent intent) {
        if (intent == null || !ACTION_EDIT_INGREDIENT.equals(intent.getAction()))
            return false;
        IngredientTypeParcel typeParcel = intent.getParcelableExtra(EXTRA_INGREDIENT_TEMPLATE_PARCEL);
        if (typeParcel == null) return false;
        String valueAsString = intent.getStringExtra(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL);
        return valueAsString != null;
    }

    @Nullable
    public String getErrorString(@NonNull String ingredientAmount) {
        Optional<IngredientDetailError> optionalError = getOptionalError(ingredientAmount);
        if (optionalError.isPresent()) {
            return resources.getString(optionalError.get().getErrorResId());
        } else {
            return null;
        }
    }

    @NonNull
    private Optional<IngredientDetailError> getOptionalError(@NonNull String ingredientAmount) {
        if (ingredientAmount.trim().isEmpty()) {
            return Optional.of(IngredientDetailError.NO_VALUE);
        } else {
            if (EnergyDensityUtils.getOrZero(ingredientAmount).compareTo(BigDecimal.ZERO) <= 0) {
                return Optional.of(IngredientDetailError.ZERO_VALUE);
            }
        }
        return Optional.absent();
    }

    public void setIngredientAmount(@NonNull String ingredientAmount) {
        ingredient.setAmount(EnergyDensityUtils.getOrZero(ingredientAmount));
    }

    public enum IngredientDetailError {
        NO_VALUE(R.string.add_ingredient_energy_density_name_error_empty),
        ZERO_VALUE(R.string.add_ingredient_energy_density_name_error_zero);

        private final int errorResId;

        IngredientDetailError(@StringRes int errorResId) {
            this.errorResId = errorResId;
        }

        @StringRes
        public int getErrorResId() {
            return errorResId;
        }

    }

    static class ParcelableProxy implements Parcelable {

        private static final BigDecimalPropertyConverter decimalConverter = new BigDecimalPropertyConverter();
        static String STATE_MODEL = "add meal ingredient details model";
        boolean isReCreated = false;
        private Ingredient ingredient;

        ParcelableProxy() {
        }

        ParcelableProxy snapshot(@NonNull IngredientDetailModel model) {
            isReCreated = false;
            this.ingredient = model.ingredient;
            return this;
        }

        public static final Creator<ParcelableProxy> CREATOR = new Creator<ParcelableProxy>() {
            @Override
            public ParcelableProxy createFromParcel(Parcel source) {
                ParcelableProxy parcelableProxy = new ParcelableProxy();
                parcelableProxy.isReCreated = true;
                long ingredientTypeId = source.readLong();
                BigDecimal value = decimalConverter.convertToEntityProperty(source.readString());
                Ingredient ingredient = new Ingredient();
                ingredient.setAmount(value);
                ingredient.setIngredientTypeId(ingredientTypeId);
                parcelableProxy.ingredient = ingredient;
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
            dest.writeLong(ingredient.getIngredientTypeId());
            dest.writeString(decimalConverter.convertToDatabaseValue(ingredient.getAmount()));
        }

    }

    public static class IngredientLoader extends Subscriber<Ingredient> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "Error loading ingredient");
        }

        @Override
        public void onNext(Ingredient ingredient) {

        }
    }

}
