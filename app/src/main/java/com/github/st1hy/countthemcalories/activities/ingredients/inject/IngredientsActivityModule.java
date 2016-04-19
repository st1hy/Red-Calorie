package com.github.st1hy.countthemcalories.activities.ingredients.inject;

import android.app.Activity;

import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenterImp;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class IngredientsActivityModule {
    private final IngredientsActivity activity;

    public IngredientsActivityModule(IngredientsActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public Activity provideActivity() {
        return activity;
    }

    @Provides
    @PerActivity
    public IngredientsView provideView() {
        return activity;
    }

    @Provides
    @PerActivity
    public IngredientsPresenter providePresenter(IngredientsPresenterImp presenter) {
        return presenter;
    }
}
