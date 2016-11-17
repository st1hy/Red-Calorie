package com.github.st1hy.countthemcalories.inject.activities.ingredients.fragment;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.IngredientsFragment;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsViewController;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.tokensearch.LastSearchResult;

import dagger.Module;
import dagger.Provides;

@Module
public class IngredientsFragmentModule {

    private final IngredientsFragment fragment;

    public IngredientsFragmentModule(IngredientsFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    public Bundle provideArguments() {
        return fragment.getArguments();
    }

    @Provides
    public IngredientsView provideView(IngredientsViewController controller) {
        return controller;
    }

    @Provides
    public View rootView() {
        return fragment.getView();
    }

    @Provides
    public Resources provideResources() {
        return fragment.getResources();
    }

    @Provides
    public FragmentActivity provideFragmentActivity() {
        return fragment.getActivity();
    }

    @Provides
    @PerFragment
    public LastSearchResult provideLastSearchResult() {
        return new LastSearchResult();
    }

    @Provides
    public RecyclerView ingredientsRecyclerView(Activity activity, IngredientsDaoAdapter adapter) {
        RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.ingredients_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }
}
