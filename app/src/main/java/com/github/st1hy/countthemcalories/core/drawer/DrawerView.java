package com.github.st1hy.countthemcalories.core.drawer;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.state.Selection;

public interface DrawerView {

    void invokeActionBack();

    boolean isDrawerOpen();

    void closeDrawer();

    void setMenuItemSelection(@IdRes int menuId, @NonNull Selection selected);

    void openDrawerActivity(@NonNull DrawerMenuItem item);

    void showNavigationAsUp();

}
