package com.github.st1hy.countthemcalories.activities.ingredients.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.fragment.IngredientsFragmentComponent;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.fragment.IngredientsFragmentModule;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.google.common.base.Preconditions;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class IngredientsFragment extends BaseFragment {
    public static final String ARG_SELECT_BOOL = "selection mode";

    IngredientsFragmentComponent component;

    @Inject
    IngredientsPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ingredients_content, container, false);
    }

    private IngredientsFragmentComponent getComponent() {
        if (component == null) {
            component = DaggerIngredientsFragmentComponent.builder()
                    .applicationComponent(getAppComponent())
                    .ingredientsFragmentModule(new IngredientsFragmentModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        getComponent().inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStart();
    }

}
