package com.github.st1hy.countthemcalories.activities.overview.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

@PerFragment
public class OverviewPresenterImp implements OverviewPresenter {

    @NonNull
    private final OverviewView view;
    @NonNull
    private final MealsPresenter adapter;
    @NonNull
    private final RecyclerViewAdapterDelegate adapterDelegate;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public OverviewPresenterImp(@NonNull OverviewView view,
                                @NonNull MealsPresenter adapter,
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
    }

    @Override
    public void onStop() {
        adapterDelegate.onStop();
        adapter.onStop();
        subscriptions.clear();
    }

}
