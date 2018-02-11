package com.github.st1hy.countthemcalories.ui.activities.addmeal.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.model.IngredientAction;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.model.ShowIngredientsInfo;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;

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
    public void showSnackbarError(@NonNull String ingredientsError) {
        getDelegate().showSnackbarError(ingredientsError);
    }

    @Override
    public void hideSnackbarError() {
        delegate.hideSnackbarError();
    }

    @Override
    public void onMealSaved(@NonNull Meal meal) {
        getDelegate().onMealSaved(meal);
    }

    @Override
    @CheckResult
    @NonNull
    public Observable.Transformer<ShowIngredientsInfo, IngredientAction> showIngredientDetails() {
        return getDelegate().showIngredientDetails();
    }

    @Override
    @NonNull
    @CheckResult
    public Observable.Transformer<Void, Ingredient> newIngredients() {
        return getDelegate().newIngredients();
    }

}
