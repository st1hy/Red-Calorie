package com.github.st1hy.countthemcalories.inject.activities.overview.fragment;

import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.MealsPresenter;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.OverviewPresenterImp;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewViewImpl;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems.MealRowComponentFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class OverviewFragmentBindings {

    @Binds
    public abstract OverviewView provideView(OverviewViewImpl view);

    @PerFragment
    @Binds
    public abstract OverviewPresenter provideDrawerPresenter(OverviewPresenterImp presenter);

    @Binds
    public abstract RecyclerAdapterWrapper adapter(MealsPresenter presenter);

    @Binds
    public abstract MealRowComponentFactory mealRowComponentFactory(OverviewFragmentComponent component);
}
