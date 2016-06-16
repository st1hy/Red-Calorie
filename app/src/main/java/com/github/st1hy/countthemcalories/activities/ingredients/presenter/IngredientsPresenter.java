package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;

public interface IngredientsPresenter extends DrawerPresenter {

    void onSelectIngredientTypeResult(int resultCode);

    void onIngredientAdded(int resultCode, @Nullable Intent data);

}
