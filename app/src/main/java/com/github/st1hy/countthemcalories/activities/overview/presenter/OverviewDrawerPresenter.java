package com.github.st1hy.countthemcalories.activities.overview.presenter;


import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.presenter.AbstractDrawerPresenter;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;

public class OverviewDrawerPresenter extends AbstractDrawerPresenter {

    public OverviewDrawerPresenter(@NonNull DrawerView view) {
        super(view);
    }

    @Override
    protected DrawerMenuItem currentItem() {
        return DrawerMenuItem.OVERVIEW;
    }

}
