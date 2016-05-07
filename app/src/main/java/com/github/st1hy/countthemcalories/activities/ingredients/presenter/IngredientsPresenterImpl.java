package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.presenter.AbstractDrawerPresenter;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class IngredientsPresenterImpl extends AbstractDrawerPresenter implements IngredientsPresenter {
    final IngredientsView view;
    final IngredientsDaoAdapter daoAdapter;
    final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public IngredientsPresenterImpl(@NonNull IngredientsView view, @NonNull IngredientsDaoAdapter daoAdapter) {
        super(view);
        this.view = view;
        this.daoAdapter = daoAdapter;
    }

    @Override
    public void onStart() {
        super.onStart();
        daoAdapter.onStart();
        onAddIngredientClicked(view.getOnAddIngredientClickedObservable());
    }

    @Override
    public void onStop() {
        super.onStop();
        daoAdapter.onStop();
        subscriptions.clear();
    }

    @Override
    protected DrawerMenuItem currentItem() {
        return DrawerMenuItem.INGREDIENTS;
    }

    @Override
    public boolean onClickedOnAction(@IdRes int actionItemId) {
        if (actionItemId == R.id.action_sorting) {
            //TODO implement sorting
            return true;
        }
        return super.onClickedOnAction(actionItemId);
    }

    void onAddIngredientClicked(@NonNull Observable<Void> observable) {
        subscribe(observable.subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.openNewIngredientScreen();
            }
        }));
    }

    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }

}
