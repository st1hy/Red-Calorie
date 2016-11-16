package com.github.st1hy.countthemcalories.inject.activities.overview.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems.MealRowComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.MealsPresenter;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.MealsPresenterImpl;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.OverviewPresenterImp;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.MealsRecyclerViewAdapter;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewViewImpl;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class OverviewFragmentModule {

    final OverviewFragment fragment;

    public OverviewFragmentModule(@NonNull OverviewFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    public OverviewView provideView(OverviewViewImpl view) {
        return view;
    }

    @PerFragment
    @Provides
    public OverviewPresenter provideDrawerPresenter(OverviewPresenterImp presenter) {
        return presenter;
    }

    @Provides
    @Named("fragmentRoot")
    public View rootView() {
        return fragment.getView();
    }

    @Provides
    public RecyclerView recyclerView(@Named("fragmentRoot") View view,
                                     RecyclerView.Adapter adapter,
                                     @Named("activityContext") Context context) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.overview_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return recyclerView;
    }

    @Provides
    @PerFragment
    public RecyclerView.Adapter adapter(MealsRecyclerViewAdapter adapter, MealsPresenter presenter) {
        presenter.setNotifier(adapter);
        return adapter;
    }

    @Provides
    public MealsPresenter mealsPresenter(MealsPresenterImpl mealsPresenter) {
        return mealsPresenter;
    }

    @Provides
    public MealRowComponentFactory mealRowComponentFactory(OverviewFragmentComponent component) {
        return component;
    }
}
