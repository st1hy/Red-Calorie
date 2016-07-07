package com.github.st1hy.countthemcalories.activities.overview.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class OverviewPresenterImp implements OverviewPresenter {

    final OverviewView view;
    final MealsAdapter adapter;
    final CompositeSubscription subscriptions = new CompositeSubscription();

    public OverviewPresenterImp(@NonNull OverviewView view, @NonNull MealsAdapter adapter) {
        this.view = view;
        this.adapter = adapter;
    }

    @Override
    public void onStart() {
        adapter.onStart();
        subscriptions.add(view.getOpenMealScreenObservable()
                .subscribe(onNewMealClicked()));
    }

    @Override
    public void onStop() {
        adapter.onStop();
        subscriptions.clear();
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
