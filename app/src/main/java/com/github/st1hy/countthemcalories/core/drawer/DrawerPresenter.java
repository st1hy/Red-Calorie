package com.github.st1hy.countthemcalories.core.drawer;


import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.google.common.base.Preconditions;

import javax.inject.Inject;

import static com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem.OVERVIEW;
import static com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem.SETTINGS;
import static com.github.st1hy.countthemcalories.core.state.Selection.SELECTED;

@PerActivity
public final class DrawerPresenter implements BasicLifecycle, NavigationView.OnNavigationItemSelectedListener {

    private final DrawerView view;
    private final DrawerMenuItem currentItem;

    @Inject
    public DrawerPresenter(@NonNull DrawerView view, @NonNull DrawerMenuItem currentItem) {
        this.view = view;
        this.currentItem = currentItem;
    }

    /**
     * @return true if back event was not handled
     */
    public boolean onBackPressed() {
        if (view.isDrawerOpen()) {
            view.closeDrawer();
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        view.setNavigationItemSelectedListener(this);
        view.setMenuItemSelection(currentItem().getMenuItemId(), SELECTED);
        if (currentItem() != OVERVIEW) {
            view.showNavigationAsUp();
        }
    }

    @Override
    public void onStop() {
        view.setNavigationItemSelectedListener(null);
    }

    /**
     * Called when user clicked on menu action button.
     *
     * @return true if action has been consumed, false to invoke default implementation
     */
    @CallSuper
    public boolean onClickedOnAction(@IdRes int actionItemId) {
        if (actionItemId == R.id.action_settings) {
            view.openDrawerActivity(SETTINGS);
            return true;
        }
        return false;
    }

    @NonNull
    public DrawerMenuItem currentItem() {
        return currentItem;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerMenuItem menuItem = DrawerMenuItem.findByMenuId(item.getItemId());
        Preconditions.checkNotNull(menuItem);
        view.closeDrawer();
        if (currentItem() != menuItem) {
            view.openDrawerActivity(menuItem);
        }
        return true;
    }
}
