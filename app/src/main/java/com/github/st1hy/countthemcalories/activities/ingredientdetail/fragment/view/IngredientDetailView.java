package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;

import rx.Observable;

public interface IngredientDetailView extends IngredientDetailScreen {

    void setName(@NonNull String name);

    void setEnergyDensity(@NonNull String readableEnergyDensity);

    void setAmount(@NonNull String readableAmount);

    void setCalorieCount(@NonNull String calorieCount);

    @NonNull
    Observable<CharSequence> getAmountObservable();

    void setAmountError(@Nullable String errorResId);

    @NonNull
    ImageView getImageView();

    void setUnitName(@NonNull String unitName);

    @NonNull
    Observable<Void> getRemoveObservable();

    @NonNull
    Observable<Void> getAcceptObservable();

    @NonNull
    String getCurrentAmount();

    void hideSoftKeyboard();
}