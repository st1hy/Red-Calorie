package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;

import javax.inject.Inject;

public class AddMealPresenterImp implements AddMealPresenter {
    private final AddMealView view;

    @Inject
    public AddMealPresenterImp(AddMealView view) {
        this.view = view;
    }

    @Override
    public void onSaveButtonClicked() {
        view.openOverviewActivity();
    }
}
