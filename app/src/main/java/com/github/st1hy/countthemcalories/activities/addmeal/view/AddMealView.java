package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;

import java.math.BigDecimal;

import rx.Observable;

public interface AddMealView extends WithPictureView {
    void setResultAndFinish();

    void openAddIngredient();

    void setName(@NonNull String name);

    @NonNull
    Observable<CharSequence> getNameObservable();

    @NonNull
    Observable<Void> getAddIngredientObservable();

    void setEmptyIngredientsVisibility(@NonNull Visibility visibility);

    void showIngredientDetails(long requestID,
                               @NonNull IngredientTypeParcel ingredientParcel,
                               @NonNull BigDecimal amount,
                               @Nullable View sharedIngredientCompact,
                               @Nullable String sharedElementName);

    void scrollTo(int itemPosition);

    void setTotalEnergy(@NonNull String totalEnergy);

    void showNameError(@Nullable String error);

    void showIngredientsError(@Nullable String ingredientsError);
}
