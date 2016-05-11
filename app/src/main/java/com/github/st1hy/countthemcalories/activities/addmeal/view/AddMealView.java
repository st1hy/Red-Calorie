package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;
import com.github.st1hy.countthemcalories.database.Ingredient;

import rx.Observable;

public interface AddMealView extends WithPictureView {
    void openOverviewActivity();

    void openAddIngredient();

    void setName(@NonNull String name);

    @NonNull Observable<CharSequence> getNameObservable();

    @NonNull Observable<Void> getAddIngredientObservable();

    void setEmptyIngredientsVisibility(@NonNull Visibility visibility);

    void showIngredientDetails(@NonNull View sharedIngredientCompact, @NonNull Ingredient ingredient);
}
