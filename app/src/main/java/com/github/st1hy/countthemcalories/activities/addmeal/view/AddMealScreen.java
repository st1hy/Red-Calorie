package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.google.common.base.Optional;

import java.math.BigDecimal;

import rx.Observable;

public interface AddMealScreen extends WithPictureView {

    @NonNull
    Observable<Void> getAddIngredientObservable();

    @NonNull
    Observable<Void> getSaveClickedObservable();

    void showSnackbarError(@NonNull Optional<String> ingredientsError);

    void onMealSaved();

    void openAddIngredient();

    void showIngredientDetails(long requestID,
                               @NonNull IngredientTypeParcel ingredientParcel,
                               @NonNull BigDecimal amount,
                               @Nullable View sharedIngredientCompact,
                               @Nullable String sharedElementName);
}
