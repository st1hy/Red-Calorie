package com.github.st1hy.countthemcalories.activities.addmeal.inject;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenterImp;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.permissions.PermissionSubject;
import com.github.st1hy.countthemcalories.core.ui.withpicture.presenter.WithPicturePresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class AddMealActivityModule {
    private final AddMealActivity activity;

    public AddMealActivityModule(@NonNull AddMealActivity activity) {
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

    @Provides
    @PerActivity
    public PermissionSubject providePermissionSubject() {
        return activity;
    }

    @Provides
    @PerActivity
    public AddMealPresenter providePresenter(AddMealPresenterImp presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public WithPicturePresenter providePicturePresenter(AddMealPresenterImp presenter) {
        return presenter;
    }
}
