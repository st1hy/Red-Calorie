package com.github.st1hy.countthemcalories.activities.addmeal.fragment.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentModule;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealLifecycleController;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealSaver;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;

import javax.inject.Inject;

public class AddMealFragment extends BaseFragment {

    private AddMealFragmentComponentFactory componentFactory;
    private AddMealFragmentComponent component;

    @Inject
    AddMealLifecycleController controller;
    @Inject
    AddMealSaver saver;
    @Inject
    RecyclerView ingredientList; //inject adapter

    public void setComponentFactory(@NonNull AddMealFragmentComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_meal_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getComponent(savedInstanceState).inject(this);
    }

    @NonNull
    protected AddMealFragmentComponent getComponent(@Nullable Bundle savedState) {
        if (component == null) {
            component = componentFactory.newAddMealFragmentComponent(new AddMealFragmentModule(this, savedState));
        }
        return component;
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
        saver.onSaveState(outState);
    }


}