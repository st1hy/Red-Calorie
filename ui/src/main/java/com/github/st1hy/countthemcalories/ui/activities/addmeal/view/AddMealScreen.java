package com.github.st1hy.countthemcalories.ui.activities.addmeal.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.model.IngredientAction;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.model.ShowIngredientsInfo;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;

import rx.Observable;

public interface AddMealScreen {

    void showSnackbarError(@NonNull String ingredientsError);

    void hideSnackbarError();

    void onMealSaved(@NonNull Meal meal);

    @CheckResult
    @NonNull
    Observable.Transformer<ShowIngredientsInfo, IngredientAction> showIngredientDetails();

    @NonNull
    @CheckResult
    Observable.Transformer<Void, Ingredient> newIngredients();

    @NonNull
    @CheckResult
    Observable<Void> getAddIngredientButtonObservable();

}
