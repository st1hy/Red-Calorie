package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.IngredientAction;
import com.github.st1hy.countthemcalories.activities.addmeal.model.ShowIngredientsInfo;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.google.common.base.Optional;

import rx.Observable;

public interface AddMealScreen {

    void showSnackbarError(@NonNull Optional<String> ingredientsError);

    void onMealSaved();

    //TODO check if this part is being correctly handled when activity restarts, since starting events depend heavily on starting parameter using compose to attach to preexisting events is challenging
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
