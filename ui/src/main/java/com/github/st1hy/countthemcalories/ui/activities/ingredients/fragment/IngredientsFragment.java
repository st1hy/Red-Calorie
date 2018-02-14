package com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.inject.IngredientsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.ui.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

import javax.inject.Inject;

public class IngredientsFragment extends BaseFragment {

    private IngredientsFragmentComponentFactory componentFactory;

    @Inject
    IngredientsPresenter presenter;
    @Inject
    RecyclerView recyclerView; //injects adapter

    public void setComponentFactory(IngredientsFragmentComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ingredients_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        componentFactory.newIngredientsFragmentComponent(new FragmentModule(this, null))
                .inject(this);
        componentFactory = null;
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
