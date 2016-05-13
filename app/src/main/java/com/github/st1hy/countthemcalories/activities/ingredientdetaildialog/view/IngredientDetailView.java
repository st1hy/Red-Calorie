package com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;

import java.math.BigDecimal;

import rx.Observable;

public interface IngredientDetailView {
    void finish();

    void setName(@NonNull String name);

    void setEnergyDensity(@NonNull String readableEnergyDensity);

    void setAmount(@NonNull String readableAmount);

    void setCalorieCount(@NonNull String calorieCount);

    @NonNull
    Observable<CharSequence> getAmountObservable();

    void setAmountError(@Nullable String errorResId);

    @NonNull
    ImageView getImageView();

    void setUnitName(@StringRes int unitName);

    @NonNull
    Observable<Void> getRemoveObservable();

    @NonNull
    Observable<Void> getAcceptObservable();

    void setResultAndFinish(int resultCode, long ingredientId,
                            @NonNull IngredientTypeParcel parcel, @NonNull BigDecimal amount);
}