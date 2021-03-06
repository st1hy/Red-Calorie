package com.github.st1hy.countthemcalories.activities.addingredient.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientLifecycleController;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientStateSaver;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import javax.inject.Inject;

public class AddIngredientFragment extends BaseFragment {

    private AddIngredientFragmentComponentFactory componentFactory;

    @Inject
    AddIngredientLifecycleController controller;
    @Inject
    AddIngredientStateSaver saver;
    @Inject
    RecyclerView tagsRecycler; //Injects tags adapter into recycler

    public void setComponentFactory(@NonNull AddIngredientFragmentComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_ingredient_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        componentFactory.newAddIngredientFragmentComponent(new FragmentModule(this, savedInstanceState))
                .inject(this);
        componentFactory = null;
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saver.onSaveState(outState);
    }
}
