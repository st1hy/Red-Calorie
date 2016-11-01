package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.model;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.property.BigDecimalPropertyConverter;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import org.parceler.Parcels;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_TEMPLATE_PARCEL;

public class IngredientDetailModel {
    final RxIngredientsDatabaseModel ingredientTypesModel;
    final ParcelableProxy parcelableProxy;
    final Resources resources;

    private final Ingredient ingredient;

    @Inject
    public IngredientDetailModel(@NonNull RxIngredientsDatabaseModel ingredientTypesModel,
                                 @NonNull Resources resources,
                                 @NonNull @Named("arguments") Bundle arguments,
                                 @Nullable @Named("savedState") Bundle savedState) {
        this.ingredientTypesModel = ingredientTypesModel;
        this.resources = resources;
        ParcelableProxy parcelableProxy = null;
        if (savedState != null) {
            parcelableProxy = savedState.getParcelable(ParcelableProxy.STATE_MODEL);
        }
        if (parcelableProxy != null) {
            ingredient = parcelableProxy.ingredient;
        } else {
            parcelableProxy = new ParcelableProxy();
            IngredientTemplate ingredientTemplate = Parcels.unwrap(arguments.getParcelable(EXTRA_INGREDIENT_TEMPLATE_PARCEL));
            Preconditions.checkNotNull(ingredientTemplate, "Missing ingredient!");
            String valueAsString = arguments.getString(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL);
            Preconditions.checkNotNull(valueAsString, "Missing amount!");
            long id = arguments.getLong(EXTRA_INGREDIENT_ID_LONG, -1L);
            ingredient = new Ingredient();
            ingredient.setId(id);
            ingredient.setAmount(new BigDecimal(valueAsString));
            ingredient.setIngredientType(ingredientTemplate);
        }
        this.parcelableProxy = parcelableProxy;
    }

    /**
     * not null and valid only when #isDataValid is true, after #getMealObservable returns any value
     */
    @NonNull
    public Ingredient getIngredient() {
        return ingredient;
    }

    public void onSaveState(@NonNull Bundle savedState) {
        savedState.putParcelable(ParcelableProxy.STATE_MODEL, parcelableProxy.snapshot(this));
    }

    @NonNull
    Observable<Ingredient> loadItem(@NonNull Ingredient ingredient) {
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

    public void setIngredientAmount(@NonNull BigDecimal ingredientAmount) {
        ingredient.setAmount(ingredientAmount);
    }

    public enum IngredientDetailError {
        NO_VALUE(R.string.add_meal_amount_error_empty),
        ZERO_VALUE(R.string.add_meal_amount_error_zero);

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
                long ingredientId = source.readLong();
                long ingredientTypeId = source.readLong();
                BigDecimal value = decimalConverter.convertToEntityProperty(source.readString());
                Ingredient ingredient = new Ingredient();
                ingredient.setId(ingredientId);
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
            dest.writeLong(ingredient.getId());
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
