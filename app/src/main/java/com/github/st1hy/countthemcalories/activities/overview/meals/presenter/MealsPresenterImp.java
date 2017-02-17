package com.github.st1hy.countthemcalories.activities.overview.meals.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.meals.view.OverviewView;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

@PerFragment
public class MealsPresenterImp implements MealsPresenter {

    @NonNull
    private final OverviewView view;
    @NonNull
    private final MealsAdapter adapter;
    @NonNull
    private final RecyclerViewAdapterDelegate adapterDelegate;
    @Inject
    FabMenuDeactivation fabMenuDeactivation;

    private final CompositeSubscription subscriptions = new CompositeSubscription();


    @Inject
    public MealsPresenterImp(@NonNull OverviewView view,
                             @NonNull MealsAdapter adapter,
                             @NonNull RecyclerViewAdapterDelegate adapterDelegate) {
        this.view = view;
        this.adapter = adapter;
        this.adapterDelegate = adapterDelegate;
    }

    @Override
    public void onStart() {
        adapterDelegate.onStart();
        adapter.onStart();
        subscriptions.add(view.getAddNewMealObservable()
                .subscribe(aVoid -> view.addNewMeal()));
        fabMenuDeactivation.onStart();
    }

    @Override
    public void onStop() {
        adapterDelegate.onStop();
        adapter.onStop();
        subscriptions.clear();
        fabMenuDeactivation.onStop();
    }

}
