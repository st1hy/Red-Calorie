package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;

import rx.Observable;

public interface IngredientsView extends DrawerView {

    void openNewIngredientScreen();

    void setNoIngredientButtonVisibility(@NonNull Visibility visibility);

    @NonNull
    Observable<Void> getOnAddIngredientClickedObservable();
}
