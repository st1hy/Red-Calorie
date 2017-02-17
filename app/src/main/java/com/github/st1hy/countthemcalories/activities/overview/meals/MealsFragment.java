package com.github.st1hy.countthemcalories.activities.overview.meals;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.meals.presenter.MealsPresenter;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.inject.activities.overview.meals.OverviewFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.overview.meals.OverviewFragmentModule;

import javax.inject.Inject;

public class MealsFragment extends BaseFragment {

    private OverviewFragmentComponentFactory componentFactory;

    @Inject
    MealsPresenter presenter;
    @Inject
    RecyclerView recyclerView; //injects adapter into recycler

    public void setComponentFactory(@NonNull OverviewFragmentComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.overview_list, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        componentFactory.newOverviewFragmentComponent(new OverviewFragmentModule(this))
                .inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }
}
