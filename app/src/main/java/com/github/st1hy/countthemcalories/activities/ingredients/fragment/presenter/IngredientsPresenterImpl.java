package com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class IngredientsPresenterImpl implements IngredientsPresenter {

    final IngredientsView view;
    final CompositeSubscription subscriptions = new CompositeSubscription();

    public IngredientsPresenterImpl(@NonNull IngredientsView view) {
        this.view = view;
    }

    @Override
    public void onStart() {
        onAddIngredientClicked(view.getOnAddIngredientClickedObservable());
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    @Override
    public void onSelectedNewIngredientType(@NonNull AddIngredientType type) {
        view.openNewIngredientScreen(type);
    }

    void onAddIngredientClicked(@NonNull Observable<Void> observable) {
        subscribe(observable.subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.selectIngredientType();
            }
        }));
    }

    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }
}
