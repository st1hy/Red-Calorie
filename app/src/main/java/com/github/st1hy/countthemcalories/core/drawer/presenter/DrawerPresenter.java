package com.github.st1hy.countthemcalories.core.drawer.presenter;

import android.support.annotation.IdRes;

public interface DrawerPresenter {

    void onBackPressed();

    /**
     * Called when user clicked on item in the drawer.
     */
    void onNavigationItemSelected(@IdRes int menuId);

    /**
     * Called when user clicked on menu action button.
     *
     * @return true if action has been consumed, false to invoke default implementation
     */
    boolean onClickedOnAction(@IdRes int actionItemId);

    void onStart();

    void onStop();
}
