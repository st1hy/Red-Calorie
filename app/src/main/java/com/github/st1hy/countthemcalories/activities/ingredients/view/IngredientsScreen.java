package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.core.baseview.DialogView;
import com.github.st1hy.countthemcalories.core.command.view.UndoView;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;

import rx.Observable;

public interface IngredientsScreen extends UndoView, SearchableView, DialogView {

    void selectIngredientType();

    @NonNull
    Observable<Void> getOnAddIngredientClickedObservable();

    void openNewIngredientScreen(@NonNull AddIngredientType type);

    void openEditIngredientScreen(long requestID, IngredientTypeParcel ingredientParcel);

    void onIngredientSelected(@NonNull IngredientTypeParcel ingredientTypeParcel);

    void openNewMealScreen(@NonNull IngredientTypeParcel parcel);
}
