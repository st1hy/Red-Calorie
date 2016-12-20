package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.IngredientAction;
import com.github.st1hy.countthemcalories.activities.addmeal.model.ShowIngredientsInfo;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.google.common.base.Optional;

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
