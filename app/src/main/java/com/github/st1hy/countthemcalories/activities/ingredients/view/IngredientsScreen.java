package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.core.command.view.UndoView;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import rx.Observable;

public interface IngredientsScreen extends UndoView, SearchableView, DialogView {

    void selectIngredientType();

    @NonNull
    Observable<Void> getOnAddIngredientClickedObservable();

    void openNewIngredientScreen(@NonNull AddIngredientType type, @NonNull String extraName);

    void openEditIngredientScreen(long requestID, IngredientTemplate ingredientTemplate);

    void onIngredientSelected(@NonNull IngredientTemplate ingredientTemplate);

    void openNewMealScreen(@NonNull IngredientTemplate ingredientTemplate);
}
