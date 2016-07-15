package com.github.st1hy.countthemcalories.core.drawer.presenter;


import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;

import static com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem.OVERVIEW;
import static com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem.SETTINGS;
import static com.github.st1hy.countthemcalories.core.state.Selection.SELECTED;

public class DrawerPresenterImpl implements DrawerPresenter {
    private final DrawerView view;
    private final DrawerMenuItem currentItem;

    public DrawerPresenterImpl(@NonNull DrawerView view, @NonNull DrawerMenuItem currentItem) {
        this.view = view;
        this.currentItem = currentItem;
    }

    @Override
    @CallSuper
    public void onBackPressed() {
        if (view.isDrawerOpen()) {
            view.closeDrawer();
        } else {
            view.invokeActionBack();
        }
    }

    @Override
    @CallSuper
    public void onNavigationItemSelected(@IdRes int menuId) {
        DrawerMenuItem menuItem = DrawerMenuItem.findByMenuId(menuId);
        assert menuItem != null;
        view.closeDrawer();
        if (currentItem() != menuItem) {
            view.openDrawerActivity(menuItem);
        }
    }

    @Override
    @CallSuper
    public void onStart() {
        view.setMenuItemSelection(currentItem().getMenuItemId(), SELECTED);
        if (currentItem() != OVERVIEW) {
            view.showNavigationAsUp();
        }
    }

    @Override
    @CallSuper
    public boolean onClickedOnAction(@IdRes int actionItemId) {
        if (actionItemId == R.id.action_settings) {
            view.openDrawerActivity(SETTINGS);
            return true;
        }
        return false;
    }

    @NonNull
    protected DrawerMenuItem currentItem() {
        return currentItem;
    }
}
