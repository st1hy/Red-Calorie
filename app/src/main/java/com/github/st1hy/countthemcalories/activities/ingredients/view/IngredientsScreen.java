package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientType;
import com.github.st1hy.countthemcalories.core.command.view.UndoView;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;

import rx.Observable;

public interface IngredientsScreen extends UndoView, SearchableView {

    void selectIngredientType();

    @NonNull
    Observable<Void> getOnAddIngredientClickedObservable();

    void openNewIngredientScreen(@NonNull AddIngredientType type);

    void openEditIngredientScreen(long requestID, IngredientTypeParcel ingredientParcel);

    void onIngredientSelected(@NonNull IngredientTypeParcel ingredientTypeParcel);

}
