package com.github.st1hy.countthemcalories.activities.overview.presenter;


import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewView;

import javax.inject.Inject;

import static com.github.st1hy.countthemcalories.core.ui.Selection.SELECTED;

public class OverviewPresenterImp implements OverviewPresenter {
    private final OverviewView view;

    @Inject
    public OverviewPresenterImp(@NonNull OverviewView view) {
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
    public void onNavigationItemSelected(@IdRes int menuId) {
        if (menuId == R.id.nav_ingredients) {
            view.openIngredientsScreen();
        }
        view.setMenuItemSelection(menuId, SELECTED);
        view.closeDrawer();
    }

    @Override
    public void onStart() {
        view.setMenuItemSelection(R.id.nav_overview, SELECTED);
    }

    @Override
    public boolean onClickedOnAction(@IdRes int actionItemId) {
        if (actionItemId == R.id.action_settings) {
            //TODO add settings
            return true;
        }
        return false;
    }

    @Override
    public void onAddMealButtonClicked() {
        view.openAddMealScreen();
    }
}
