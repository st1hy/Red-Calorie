package com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.ui.activities.ingredients.model.AddIngredientParams;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.view.IngredientsScreen;
import com.github.st1hy.countthemcalories.database.commands.UndoAction;
import com.github.st1hy.countthemcalories.database.commands.UndoView;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import rx.Observable;

abstract class IngredientsViewDelegate implements IngredientsView {

    @NonNull
    private final UndoView undoView;
    @NonNull
    private final IngredientsScreen screen;

    IngredientsViewDelegate(@NonNull UndoView undoView, @NonNull IngredientsScreen screen) {
        this.undoView = undoView;
        this.screen = screen;
    }

    @CheckResult
    @NonNull
    @Override
    public Observable<UndoAction> showUndoMessage(@StringRes int undoMessageResId) {
        return undoView.showUndoMessage(undoMessageResId);
    }

    @Override
    public void hideUndoMessage() {
        undoView.hideUndoMessage();
    }


    @Override
    @NonNull
    @CheckResult
    public Observable<Void> getOnAddIngredientClickedObservable() {
        return screen.getOnAddIngredientClickedObservable();
    }

    @Override
    @NonNull
    @CheckResult
    public Observable.Transformer<AddIngredientParams, IngredientTemplate> addNewIngredient() {
        return screen.addNewIngredient();
    }

    @Override
    public void editIngredientTemplate(long requestID, IngredientTemplate ingredientTemplate) {
        screen.editIngredientTemplate(requestID, ingredientTemplate);
    }

    @Override
    public void onIngredientSelected(@NonNull IngredientTemplate ingredientTemplate) {
        screen.onIngredientSelected(ingredientTemplate);
    }

    @Override
    public void addToNewMeal(@NonNull IngredientTemplate ingredientTemplate) {
        screen.addToNewMeal(ingredientTemplate);
    }
}
