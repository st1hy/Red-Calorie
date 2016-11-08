package com.github.st1hy.countthemcalories.activities.addingredient.fragment.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentModule;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.DaggerAddIngredientFragmentComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientStateSaver;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.google.common.base.Preconditions;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class AddIngredientFragment extends BaseFragment {

    AddIngredientFragmentComponent component;

    @Inject
    AddIngredientPresenter presenter;
    @Inject
    AddIngredientStateSaver saver;
    @Inject
    RecyclerView tagsRecycler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_ingredient_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        getComponent(savedInstanceState).inject(this);
    }

    @NonNull
    protected AddIngredientFragmentComponent getComponent(@Nullable Bundle savedState) {
        if (component == null) {
            component = DaggerAddIngredientFragmentComponent.builder()
                    .applicationComponent(getAppComponent())
                    .addIngredientFragmentModule(new AddIngredientFragmentModule(this, savedState))
                    .build();
        }
        return component;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saver.onSaveState(outState);
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
