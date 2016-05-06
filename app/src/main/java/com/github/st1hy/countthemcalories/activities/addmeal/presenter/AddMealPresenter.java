package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenter;

public interface AddMealPresenter extends WithPicturePresenter {

    @NonNull
    RecyclerView.Adapter getIngredientListAdapter();

    void onAddNewIngredientClicked();

    void onIngredientReceived(int ingredientTypeId);

    boolean onClickedOnAction(@IdRes int menuActionId);
}
