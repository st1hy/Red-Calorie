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

public abstract class AddMealScreenDelegate implements AddMealScreen {

    @NonNull
    private final AddMealScreen delegate;

    public AddMealScreenDelegate(@NonNull AddMealScreen delegate) {
        this.delegate = delegate;
    }

    @NonNull
    protected AddMealScreen getDelegate() {
        return delegate;
    }

    @Override
    @CheckResult
    @NonNull
    public Observable<Void> getAddIngredientButtonObservable() {
        return getDelegate().getAddIngredientButtonObservable();
    }

    @Override
    public void showSnackbarError(@NonNull Optional<String> ingredientsError) {
        getDelegate().showSnackbarError(ingredientsError);
    }

    @Override
    public void onMealSaved() {
        getDelegate().onMealSaved();
    }

    @Override
    @CheckResult
    @NonNull
    public Observable<IngredientAction> showIngredientDetails(long requestId, @NonNull Ingredient ingredient, @NonNull List<Pair<View, String>> sharedElements) {
        return getDelegate().showIngredientDetails(requestId, ingredient, sharedElements);
    }

    @Override
    @NonNull
    @CheckResult
    public Observable<Ingredient> addIngredient() {
        return getDelegate().addIngredient();
    }

}
