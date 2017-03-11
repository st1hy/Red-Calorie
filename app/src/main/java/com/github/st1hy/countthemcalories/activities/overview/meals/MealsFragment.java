package com.github.st1hy.countthemcalories.activities.overview.meals;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.overview.meals.presenter.MealsPresenter;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import javax.inject.Inject;

public class MealsFragment extends BaseFragment {

    public static final String ARG_CURRENT_PAGE = "current page";

    @Inject
    MealsPresenter presenter;
    @Inject
    RecyclerView recyclerView; //injects adapter into recycler

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.overview_list, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OverviewActivity activity = (OverviewActivity) getActivity();
        activity.getMealsFragmentComponentFactory()
                .newOverviewFragmentComponent(new FragmentModule(this, savedInstanceState))
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
