package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.view.DrawerView;

import rx.Observable;

public interface IngredientsView extends DrawerView {

    void openOverviewScreen();

    void openNewIngredientScreen();

    void openSettingsScreen();

    void openTagsScreen();

    void setNoIngredientButtonVisibility(@NonNull Visibility visibility);

    @NonNull
    Observable<Void> getOnAddIngredientClickedObservable();
}
