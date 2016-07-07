package com.github.st1hy.countthemcalories.activities.overview.fragment.inject;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.MealsViewModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.commands.MealsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.MealsAdapter;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.OverviewPresenterImp;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

import static com.google.common.base.Preconditions.checkState;

@Module
public class OverviewFragmentModule {
    final OverviewFragment fragment;

    public OverviewFragmentModule(@NonNull OverviewFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    public OverviewView provideView() {
        return fragment;
    }

    @Provides
    @PerFragment
    public OverviewScreen provideOverviewScreen() {
        FragmentActivity activity = fragment.getActivity();
        checkState(activity instanceof OverviewScreen, "activity must implement screen controls: " + OverviewScreen.class.getSimpleName());
        return (OverviewScreen) activity;
    }

    @PerFragment
    @Provides
    public MealsAdapter provideAdapter(OverviewView view, RxMealsDatabaseModel databaseModel,
                                       Picasso picasso, PhysicalQuantitiesModel quantityModel,
                                       MealsDatabaseCommands commands, MealsViewModel viewModel) {
        return new MealsAdapter(view, databaseModel, picasso, quantityModel, commands, viewModel);
    }

    @PerFragment
    @Provides
    public OverviewPresenter provideDrawerPresenter(OverviewView view, MealsAdapter adapter) {
        return new OverviewPresenterImp(view, adapter);
    }

    @PerFragment
    @Provides
    public Resources provideResources() {
        return fragment.getResources();
    }
}
