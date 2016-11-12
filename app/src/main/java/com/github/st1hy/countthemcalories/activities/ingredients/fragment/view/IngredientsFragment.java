package com.github.st1hy.countthemcalories.activities.ingredients.fragment.view;

import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject.DaggerIngredientsFragmentComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject.IngredientsFragmentComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject.IngredientsFragmentModule;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsScreen;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.google.common.base.Preconditions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

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
