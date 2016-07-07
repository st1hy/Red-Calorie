package com.github.st1hy.countthemcalories.activities.mealdetail.inject;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.model.MealDetailModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.presenter.MealDetailPresenter;
import com.github.st1hy.countthemcalories.activities.mealdetail.presenter.MealDetailPresenterImpl;
import com.github.st1hy.countthemcalories.activities.mealdetail.presenter.MealIngredientsAdapter;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailView;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class MealDetailsModule {
    private final MealDetailActivity activity;
    private final Bundle bundle;

    public MealDetailsModule(@NonNull MealDetailActivity activity, @Nullable Bundle bundle) {
        this.activity = activity;
        this.bundle = bundle;
    }

    @Provides
    @PerActivity
    @Nullable
    public Bundle provideBundle() {
        return bundle;
    }

    @Provides
    @PerActivity
    public MealDetailView provideView() {
        return activity;
    }

    @Provides
    @PerActivity
    public MealDetailModel provideModel(@NonNull RxMealsDatabaseModel databaseModel,
                                        @Nullable Intent intent,
                                        @Nullable Bundle savedState) {
        return new MealDetailModel(databaseModel, intent, savedState);
    }

    @Provides
    @PerActivity
    public MealIngredientsAdapter provideAdapter(MealDetailModel model,
                                                 PhysicalQuantitiesModel quantitiesModel) {
        return new MealIngredientsAdapter(model, quantitiesModel);
    }

    @Provides
    @PerActivity
    public MealDetailPresenter providePresenter(MealDetailPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public Intent provideIntent() {
        return activity.getIntent();
    }

    @Provides
    @PerActivity
    public Resources provideResources() {
        return activity.getResources();
    }
}
