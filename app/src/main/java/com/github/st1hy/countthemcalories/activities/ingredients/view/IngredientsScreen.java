package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.ingredients.model.AddIngredientParams;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import rx.Observable;

public interface IngredientsScreen {

    @NonNull
    @CheckResult
    Observable.Transformer<Void, AddIngredientType> selectIngredientType();

    @NonNull
    @CheckResult
    Observable<Void> getOnAddIngredientClickedObservable();

    @NonNull
    @CheckResult
    Observable.Transformer<AddIngredientParams, IngredientTemplate> addNewIngredient();

    void editIngredientTemplate(long requestID, IngredientTemplate ingredientTemplate);

    void onIngredientSelected(@NonNull IngredientTemplate ingredientTemplate);

    void addToNewMeal(@NonNull IngredientTemplate ingredientTemplate);
}
