package com.github.st1hy.countthemcalories.inject.activities.overview.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.fragment.OverviewFragment;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.MealsPresenter;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.OverviewPresenterImp;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewViewImpl;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems.MealRowComponentFactory;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class OverviewFragmentModule {

    @NonNull
    private final OverviewFragment fragment;

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
                                     RecyclerViewAdapterDelegate adapter,
                                     @Named("activityContext") Context context) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.overview_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return recyclerView;
    }

    @Provides
    public RecyclerAdapterWrapper adapter(MealsPresenter presenter) {
        return presenter;
    }

    @Provides
    public MealRowComponentFactory mealRowComponentFactory(OverviewFragmentComponent component) {
        return component;
    }

}
