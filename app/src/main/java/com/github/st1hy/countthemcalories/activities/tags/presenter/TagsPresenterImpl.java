package com.github.st1hy.countthemcalories.activities.tags.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.presenter.AbstractDrawerPresenter;

import javax.inject.Inject;

public class TagsPresenterImpl extends AbstractDrawerPresenter implements TagsPresenter {
    final TagsView view;
    final TagsDaoAdapter adapter;


    @Inject
    public TagsPresenterImpl(@NonNull TagsView view, @NonNull TagsDaoAdapter adapter) {
        super(view);
        this.view = view;
        this.adapter = adapter;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.onStop();
    }

    @Override
    protected DrawerMenuItem currentItem() {
        return DrawerMenuItem.CATEGORIES;
    }
}
