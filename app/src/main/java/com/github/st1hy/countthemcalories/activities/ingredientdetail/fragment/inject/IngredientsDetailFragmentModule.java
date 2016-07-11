package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailView;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.google.common.base.Preconditions;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class IngredientsDetailFragmentModule {

    public static final String EXTRA_INGREDIENT_TEMPLATE_PARCEL = "ingredient details extra template parcel";
    public static final String EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL = "ingredient details extra amount";
    public static final String EXTRA_INGREDIENT_ID_LONG = "ingredient details extra id long";

    private final IngredientDetailFragment fragment;
    @Nullable
    private final Bundle savedState;

    public IngredientsDetailFragmentModule(@NonNull IngredientDetailFragment fragment,
                                           @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    @PerFragment
    public IngredientDetailPresenter providePresenter(IngredientDetailPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    public IngredientDetailView provideView() {
        return fragment;
    }

    @Provides
    public Resources provideResources() {
        return fragment.getResources();
    }

    @Provides
    @Named("savedState")
    @Nullable
    public Bundle provideSavedState() {
        return savedState;
    }

    @Provides
    @Named("arguments")
    public Bundle provideArguments() {
        return Preconditions.checkNotNull(fragment.getArguments());
    }

    @Provides
    public IngredientDetailScreen provideScreen() {
        FragmentActivity activity = fragment.getActivity();
        Preconditions.checkArgument(activity instanceof  IngredientDetailScreen,
                "activity must implement " + IngredientDetailScreen.class.getSimpleName());
        return (IngredientDetailScreen) activity;
    }
}
