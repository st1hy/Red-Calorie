package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealDetailPresenter;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealDetailPresenterImpl;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealIngredientsAdapter;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailFragment;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailView;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailViewImpl;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.WithoutPlaceholderImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.database.Meal;

import org.parceler.Parcels;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class MealDetailsModule {

    private final MealDetailFragment fragment;
    private final Bundle savedState;

    public MealDetailsModule(@NonNull MealDetailFragment fragment, @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    @Nullable
    public Bundle provideBundle() {
        return savedState;
    }

    @Provides
    public MealDetailView provideView(MealDetailViewImpl mealDetailView) {
        return mealDetailView;
    }

    @Provides
    public MealDetailPresenter providePresenter(MealDetailPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    @PerFragment
    public Meal provideMeal() {
        return Parcels.unwrap(fragment.getArguments().getParcelable(MealDetailFragment.ARG_MEAL_PARCEL));
    }

    @Provides
    public Resources provideResources() {
        return fragment.getResources();
    }

    @Provides
    @PerFragment
    public ImageHolderDelegate provideImageHolder(WithoutPlaceholderImageHolderDelegate imageHolderDelegate) {
        return imageHolderDelegate;
    }

    @Provides
    @PerFragment
    public ImageView provideImageViewProvider(View rootView) {
        return (ImageView) rootView.findViewById(R.id.meal_detail_image);
    }

    @Provides
    public FragmentActivity provideFragmentActivity() {
        return fragment.getActivity();
    }

    @Provides
    public View rooView() {
        return fragment.getView();
    }

    @Provides
    @PerFragment
    public RecyclerView recyclerView(@Named("activityContext") Context context, View rootView, MealIngredientsAdapter adapter) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.meal_detail_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return recyclerView;
    }

}
