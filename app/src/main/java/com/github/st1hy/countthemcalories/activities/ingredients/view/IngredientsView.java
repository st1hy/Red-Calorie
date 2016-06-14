package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.core.command.view.UndoView;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;

import rx.Observable;

public interface IngredientsView extends DrawerView, UndoView {

    void openNewIngredientScreen(@NonNull String action);

    void setNoIngredientsVisibility(@NonNull Visibility visibility);

    @NonNull
    Observable<Void> getOnAddIngredientClickedObservable();

    void setResultAndReturn(@NonNull IngredientTypeParcel ingredientTypeParcel);

    void selectIngredientType();

    /**
     * @return observable that emits one item when action is confirmed by the user
     */
    @NonNull
    Observable<Void> showUsedIngredientRemoveConfirmationDialog();

    void openEditIngredientScreen(long requestID, IngredientTypeParcel ingredientParcel);

    void scrollToPosition(int position);

    void setNoIngredientsMessage(@StringRes int noIngredientTextResId);
}
