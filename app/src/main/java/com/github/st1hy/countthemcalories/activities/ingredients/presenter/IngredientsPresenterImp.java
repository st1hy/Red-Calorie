package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.support.annotation.IdRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;

import javax.inject.Inject;

import static com.github.st1hy.countthemcalories.core.ui.Selection.SELECTED;

public class IngredientsPresenterImp implements IngredientsPresenter {
    private final IngredientsView view;

    @Inject
    public IngredientsPresenterImp(IngredientsView view) {
        this.view = view;
    }

    @Override
    public void onBackPressed() {
        if (view.isDrawerOpen()) {
            view.closeDrawer();
        } else {
            view.invokeActionBack();
        }
    }

    @Override
    public void onStart() {
        view.setMenuItemSelection(R.id.nav_ingredients, SELECTED);
    }

    @Override
    public void onNavigationItemSelected(@IdRes int itemId) {
        if (itemId == R.id.nav_overview) {
            view.openOverviewScreen();
        } else if (itemId == R.id.nav_settings) {
            view.openSettingsScreen();
        }
        view.setMenuItemSelection(itemId, SELECTED);
        view.closeDrawer();
    }

    @Override
    public boolean onClickedOnAction(@IdRes int actionItemId) {
        if (actionItemId == R.id.action_sorting) {
            return true;
        }
        return false;
    }

    @Override
    public void onAddNewIngredientClicked() {
        view.openNewIngredientScreen();
    }
}
