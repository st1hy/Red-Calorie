package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.DaggerIngredientDetailFragmentComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientDetailFragmentComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.google.common.base.Preconditions;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class IngredientDetailFragment extends BaseFragment {

    IngredientDetailFragmentComponent component;

    @Inject
    IngredientDetailPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ingredient_detail_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        getComponent(savedInstanceState).inject(this);
    }

    protected IngredientDetailFragmentComponent getComponent(@Nullable Bundle savedState) {
        if (component == null) {
            component = DaggerIngredientDetailFragmentComponent.builder()
                    .applicationComponent(getAppComponent())
                    .ingredientsDetailFragmentModule(new IngredientsDetailFragmentModule(this, savedState))
                    .build();
        }
        return component;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveState(outState);
    }


}
