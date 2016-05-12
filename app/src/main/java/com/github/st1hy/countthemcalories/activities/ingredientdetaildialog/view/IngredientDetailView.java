package com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
}