package com.github.st1hy.countthemcalories.activities.overview.presenter;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewView;
import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.presenter.AbstractDrawerPresenter;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

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

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == OverviewActivity.REQUEST_MEAL_DETAIL) {
            if (!handleResultMealDetail(resultCode, data)) {
                Timber.w("Incorrect result from meal detail activity, code:%d, data:%s", requestCode, data);
            }
            return true;
        }
        return false;
    }

    private boolean handleResultMealDetail(int resultCode, @Nullable Intent data) {
        if (data == null) return false;
        long mealId = data.getLongExtra(MealDetailActivity.EXTRA_RESULT_MEAL_ID_LONG, -2L);
        if (mealId == -2L) return false;
        switch (resultCode) {
            case MealDetailActivity.RESULT_EDIT:
                adapter.editMealWithId(mealId);
                break;
            case MealDetailActivity.RESULT_DELETE:
                adapter.deleteMealWithId(mealId);
                break;
            default: return false;
        }
        return true;
    }
}
