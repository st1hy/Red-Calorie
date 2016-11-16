package com.github.st1hy.countthemcalories.activities.overview.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class OverviewPresenterImp implements OverviewPresenter {

    private final OverviewView view;
    private final MealsPresenter adapter;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public OverviewPresenterImp(@NonNull OverviewView view, @NonNull MealsPresenter adapter) {
        this.view = view;
        this.adapter = adapter;
    }

    @Override
    public void onStart() {
        adapter.onStart();
        subscriptions.add(view.getAddNewMealObservable()
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.addNewMeal();
                    }
                }));
    }

    @Override
    public void onStop() {
        adapter.onStop();
        subscriptions.clear();
    }

}
