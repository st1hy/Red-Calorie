package com.github.st1hy.countthemcalories.activities.tags.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.presenter.AbstractDrawerPresenter;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;

import javax.inject.Inject;

public class TagsDrawerPresenter extends AbstractDrawerPresenter {

    @Inject
    public TagsDrawerPresenter(@NonNull DrawerView view) {
        super(view);
    }

    @Override
    protected DrawerMenuItem currentItem() {
        return DrawerMenuItem.CATEGORIES;
    }
}
