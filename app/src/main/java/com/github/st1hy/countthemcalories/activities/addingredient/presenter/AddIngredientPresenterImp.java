package com.github.st1hy.countthemcalories.activities.addingredient.presenter;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientView;
import com.github.st1hy.countthemcalories.activities.withpicture.presenter.WithPicturePresenterImp;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;

import javax.inject.Inject;

public class AddIngredientPresenterImp extends WithPicturePresenterImp implements AddIngredientPresenter {
    private final AddIngredientView view;

    @Inject
    public AddIngredientPresenterImp(@NonNull AddIngredientView view,
                                     @NonNull PermissionsHelper permissionsHelper) {
        super(view, permissionsHelper);
        this.view = view;
    }

    @Override
    public boolean onClickedOnAction(@IdRes int menuActionId) {
        if (menuActionId == R.id.action_save) {
            onSaveActionClicked();
        }
        return false;
    }

    @Override
    public void onSelectUnitClicked() {
        view.showAvailableUnitsDialog();
    }

    public void onSaveActionClicked() {
        view.openIngredientsScreen();
    }
}
