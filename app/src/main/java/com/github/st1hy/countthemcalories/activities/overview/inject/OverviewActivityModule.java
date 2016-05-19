package com.github.st1hy.countthemcalories.activities.overview.inject;

import android.content.res.Resources;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.presenter.MealsAdapter;
import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewPresenterImp;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewView;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module
public class OverviewActivityModule {
    private OverviewActivity activity;


    public OverviewActivityModule(OverviewActivity activity) {
        this.activity = activity;
    }

    @PerActivity
    @Provides
    public OverviewView provideView() {
        return activity;
    }


    @PerActivity
    @Provides
    public MealsAdapter provideAdapter(OverviewView view, MealDatabaseModel databaseModel,
                                       Picasso picasso, PhysicalQuantitiesModel quantityModel) {
        return new MealsAdapter(view, databaseModel, picasso, quantityModel);
    }

    @PerActivity
    @Provides
    public OverviewPresenterImp providePresenter(OverviewView view, MealsAdapter adapter) {
        return new OverviewPresenterImp(view, adapter);
    }

    @PerActivity
    @Provides
    public OverviewPresenter provideOverviewPresenter(OverviewPresenterImp presenter) {
        return presenter;
    }

    @PerActivity
    @Provides
    public DrawerPresenter provideDrawerPresenter(OverviewPresenterImp presenter) {
        return presenter;
    }

    @PerActivity
    @Provides
    public Resources provideResources() {
        return activity.getResources();
    }

}
