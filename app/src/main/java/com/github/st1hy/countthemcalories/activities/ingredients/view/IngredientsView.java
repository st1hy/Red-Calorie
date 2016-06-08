package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;

import rx.Observable;

public interface IngredientsView extends DrawerView {

    void openNewIngredientScreen(@NonNull String action);

    void setNoIngredientButtonVisibility(@NonNull Visibility visibility);

    @NonNull
    Observable<Void> getOnAddIngredientClickedObservable();

    void setResultAndReturn(@NonNull IngredientTypeParcel ingredientTypeParcel);

    void selectIngredientType();

    @NonNull
    Observable<Void> showUsedIngredientRemoveConfirmationDialog();

    void openEditIngredientScreen(long requestID, IngredientTypeParcel ingredientParcel);

    void scrollToPosition(int position);
}
