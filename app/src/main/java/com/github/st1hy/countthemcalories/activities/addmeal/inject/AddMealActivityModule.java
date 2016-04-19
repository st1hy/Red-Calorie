package com.github.st1hy.countthemcalories.activities.addmeal.inject;

import android.app.Activity;

import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenterImp;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class AddMealActivityModule {
    private final AddMealActivity activity;

    public AddMealActivityModule(AddMealActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public Activity provideActivity() {
        return activity;
    }

    @Provides
    @PerActivity
    public AddMealView provideView() {
        return activity;
    }

    @Provides @PerActivity
    public AddMealPresenter providePresenter(AddMealPresenterImp presenter) {
        return presenter;
    }
}
