package com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;

public interface IngredientsPresenter {

    void onStart();

    void onStop();

    void onSelectedNewIngredientType(@NonNull AddIngredientType type);
}
