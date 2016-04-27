package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.withpicture.presenter.WithPicturePresenterImp;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;

import javax.inject.Inject;

public class AddMealPresenterImp extends WithPicturePresenterImp implements AddMealPresenter {
    private final AddMealView view;
    private final IngredientListPresenter ingredientListPresenter;

    @Inject
    public AddMealPresenterImp(@NonNull AddMealView view,
                               @NonNull PermissionsHelper permissionsHelper,
                               @NonNull IngredientListPresenter ingredientListPresenter) {
        super(view, permissionsHelper);
        this.view = view;
        this.ingredientListPresenter = ingredientListPresenter;
    }

    public void onSaveButtonClicked() {
        view.openOverviewActivity();
    }

    @NonNull
    @Override
    public RecyclerView.Adapter getIngredientListAdapter() {
        return ingredientListPresenter;
    }

    @Override
    public void onAddNewIngredientClicked() {
        view.openAddIngredient();
    }

    @Override
    public void onIngredientReceived(int ingredientTypeId) {
        //TODO
    }

    @Override
    public boolean onClickedOnAction(@IdRes int menuActionId) {
        if (menuActionId == R.id.action_save) {
            onSaveButtonClicked();
            return true;
        }
        return false;
    }

}
