package com.github.st1hy.countthemcalories.activities.overview.presenter;


import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.view.OverviewView;
import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.presenter.AbstractDrawerPresenter;

import javax.inject.Inject;

public class OverviewPresenterImp extends AbstractDrawerPresenter implements OverviewPresenter {
    private final OverviewView view;

    @Inject
    public OverviewPresenterImp(@NonNull OverviewView view) {
        super(view);
        this.view = view;
    }

    @Override
    public void onAddMealButtonClicked() {
        view.openAddMealScreen();
    }

    @Override
    protected DrawerMenuItem currentItem() {
        return DrawerMenuItem.OVERVIEW;
    }
}
