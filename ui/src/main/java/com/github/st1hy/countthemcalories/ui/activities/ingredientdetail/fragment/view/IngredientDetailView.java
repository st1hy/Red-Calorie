package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.fragment.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.view.IngredientDetailScreen;

import rx.Observable;

public interface IngredientDetailView extends IngredientDetailScreen {

    void setName(@NonNull String name);

    void setEnergyDensity(@NonNull String readableEnergyDensity);

    void setAmount(@NonNull String readableAmount);

    void setCalorieCount(@NonNull String calorieCount);

    @NonNull
    @CheckResult
    Observable<CharSequence> getAmountObservable();

    void setAmountError(@Nullable String errorResId);

    @NonNull
    ImageView getImageView();

    void setUnitName(@NonNull String unitName);

    @NonNull
    @CheckResult
    Observable<Void> getRemoveObservable();

    @NonNull
    @CheckResult
    Observable<Void> getAcceptObservable();

    @NonNull
    String getCurrentAmount();

    void hideSoftKeyboard();
}