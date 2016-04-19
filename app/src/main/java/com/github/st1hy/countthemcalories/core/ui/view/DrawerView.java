package com.github.st1hy.countthemcalories.core.ui.view;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.ui.Selection;

public interface DrawerView extends BaseView {

    void invokeActionBack();

    boolean isDrawerOpen();

    void closeDrawer();

    void setMenuItemSelection(@IdRes int menuId, @NonNull Selection selected);
}
