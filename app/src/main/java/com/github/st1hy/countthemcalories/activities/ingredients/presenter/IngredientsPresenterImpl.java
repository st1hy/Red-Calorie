package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.SelectIngredientTypeActivity;
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
    public void onSelectIngredientTypeResult(int resultCode) {
        switch (resultCode) {
            case SelectIngredientTypeActivity.RESULT_DRINK:
                view.openNewIngredientScreen(AddIngredientActivity.ACTION_CREATE_DRINK);
                break;
            case SelectIngredientTypeActivity.RESULT_MEAL:
                view.openNewIngredientScreen(AddIngredientActivity.ACTION_CREATE_MEAL);
                break;
        }
    }

    @Override
    public void onIngredientAdded(int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            long addedIngredientId = data.getLongExtra(AddIngredientActivity.RESULT_INGREDIENT_ID_LONG, -1L);
            if (addedIngredientId != -1L)
                daoAdapter.onIngredientAdded(addedIngredientId);
        }
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
                view.selectIngredientType();
            }
        }));
    }

    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }
}
