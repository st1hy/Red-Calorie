package com.github.st1hy.countthemcalories.core.drawer.view;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.state.Selection;
import com.github.st1hy.countthemcalories.core.baseview.BaseView;

public interface DrawerView extends BaseView {

    void invokeActionBack();

    boolean isDrawerOpen();

    void closeDrawer();

    void setMenuItemSelection(@IdRes int menuId, @NonNull Selection selected);

    void openDrawerActivity(@NonNull DrawerMenuItem item);

    void showNavigationAsUp();

}
