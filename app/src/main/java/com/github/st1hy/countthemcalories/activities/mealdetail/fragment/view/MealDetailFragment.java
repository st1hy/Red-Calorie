package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject.MealDetailComponent;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject.MealDetailsModule;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.LifecycleController;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.google.common.base.Preconditions;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class MealDetailFragment extends BaseFragment {

    public static final String ARG_MEAL_PARCEL = "meal detail parcel";

    MealDetailComponent component;

    @Inject
    LifecycleController controller;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meal_detail_content, container, false);
    }

    @NonNull
    protected MealDetailComponent getComponent(@Nullable Bundle savedInstanceState) {
        if (component == null) {
            MealDetailActivity activity = (MealDetailActivity) getActivity();
            component = activity.getComponent()
                    .mealDetailComponent(new MealDetailsModule(this, savedInstanceState));
        }
        return component;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        getComponent(savedInstanceState).inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        controller.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        controller.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        controller.onSaveState(outState);
    }


}
