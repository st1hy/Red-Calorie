package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.IngredientAction;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.google.common.base.Optional;

import java.util.List;

import rx.Observable;

public interface AddMealScreen {

    void showSnackbarError(@NonNull Optional<String> ingredientsError);

    void onMealSaved();

    @CheckResult
    @NonNull
    Observable<IngredientAction> showIngredientDetails(long requestId,
                                                       @NonNull Ingredient ingredient,
                                                       @NonNull List<Pair<View, String>> sharedElements);

    @NonNull
    @CheckResult
    Observable<Ingredient> addIngredient();

    @NonNull
    @CheckResult
    Observable<Void> getAddIngredientButtonObservable();


}
