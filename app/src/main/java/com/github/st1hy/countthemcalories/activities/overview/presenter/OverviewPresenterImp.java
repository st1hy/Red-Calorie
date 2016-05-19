package com.github.st1hy.countthemcalories.activities.overview.presenter;


import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.view.OverviewView;
import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.presenter.AbstractDrawerPresenter;
import com.github.st1hy.countthemcalories.core.state.Visibility;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class OverviewPresenterImp extends AbstractDrawerPresenter implements OverviewPresenter {
    final OverviewView view;
    final MealsAdapter adapter;
    final CompositeSubscription subscriptions = new CompositeSubscription();

    public OverviewPresenterImp(@NonNull OverviewView view, @NonNull MealsAdapter adapter) {
        super(view);
        this.view = view;
        this.adapter = adapter;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.onStart();
        subscriptions.add(view.getOpenMealScreenObservable()
                .subscribe(onNewMealClicked()));
        subscriptions.add(view.getDismissEmptyListVariationObservable()
                .subscribe(onDismissEmptyVariationClicked()));
    }

    private Action1<? super Void> onDismissEmptyVariationClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.setEmptyListVariationVisibility(Visibility.GONE);
                view.setEmptyListVisibility(Visibility.VISIBLE);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.onStop();
        subscriptions.clear();
    }

    @Override
    protected DrawerMenuItem currentItem() {
        return DrawerMenuItem.OVERVIEW;
    }

    @NonNull
    private Action1<Void> onNewMealClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.openAddMealScreen();
            }
        };
    }
}
