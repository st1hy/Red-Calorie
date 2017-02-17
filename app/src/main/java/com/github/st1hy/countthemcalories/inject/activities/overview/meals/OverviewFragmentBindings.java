package com.github.st1hy.countthemcalories.inject.activities.overview.meals;

import com.github.st1hy.countthemcalories.activities.overview.meals.presenter.MealsAdapter;
import com.github.st1hy.countthemcalories.activities.overview.meals.presenter.MealsPresenter;
import com.github.st1hy.countthemcalories.activities.overview.meals.presenter.MealsPresenterImp;
import com.github.st1hy.countthemcalories.activities.overview.meals.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.meals.view.OverviewViewImpl;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.overview.meals.mealitems.MealRowComponentFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class OverviewFragmentBindings {

    @Binds
    public abstract OverviewView provideView(OverviewViewImpl view);

    @PerFragment
    @Binds
    public abstract MealsPresenter provideDrawerPresenter(MealsPresenterImp presenter);

    @Binds
    public abstract RecyclerAdapterWrapper adapter(MealsAdapter presenter);

    @Binds
    public abstract MealRowComponentFactory mealRowComponentFactory(OverviewFragmentComponent component);
}
