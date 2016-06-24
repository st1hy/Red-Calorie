package com.github.st1hy.countthemcalories.activities.contribute.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.contribute.view.ContributeView;
import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.presenter.AbstractDrawerPresenter;

import javax.inject.Inject;

public class ContributePresenter extends AbstractDrawerPresenter {

    @Inject
    public ContributePresenter(@NonNull ContributeView view) {
        super(view);
    }


    @Override
    protected DrawerMenuItem currentItem() {
        return DrawerMenuItem.CONTRIBUTE;
    }
}
