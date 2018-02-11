package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.fragment.inject.IngredientDetailFragmentComponentFactory;
import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.ui.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

import javax.inject.Inject;

public class IngredientDetailFragment extends BaseFragment {

    private IngredientDetailFragmentComponentFactory componentFactory;

    @Inject
    IngredientDetailPresenter presenter;

    public void setComponentFactory(
            @NonNull IngredientDetailFragmentComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ingredient_detail_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        componentFactory.newIngredientDetailFragmentComponent(new FragmentModule(this, savedInstanceState))
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveState(outState);
    }


}
