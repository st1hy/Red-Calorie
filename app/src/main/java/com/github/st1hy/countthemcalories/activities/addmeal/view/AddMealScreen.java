package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.google.common.base.Optional;

import java.math.BigDecimal;
import java.util.List;

import rx.Observable;

public interface AddMealScreen extends WithPictureView {

    @NonNull
    Observable<Void> getAddIngredientObservable();

    @NonNull
    Observable<Void> getSaveClickedObservable();

    void showSnackbarError(@NonNull Optional<String> ingredientsError);

    void onMealSaved();

    void openAddIngredient();

    void showIngredientDetails(long requestId,
                               @NonNull IngredientTypeParcel ingredientParcel,
                               @NonNull BigDecimal amount,
                               @NonNull List<Pair<View, String>> sharedElements);

    ImageView getImageView();
}
