package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealDetailPresenter;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealDetailPresenterImpl;
import com.github.st1hy.countthemcalories.core.picture.imageholder.WithoutPlaceholderImageHolderDelegate;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealIngredientsAdapter;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailFragment;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailView;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailScreen;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.picture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import javax.inject.Provider;

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
    public MealDetailView provideView() {
        return fragment;
    }


    @Provides
    @PerFragment
    public MealIngredientsAdapter provideAdapter(Meal meal,
                                                 PhysicalQuantitiesModel quantitiesModel) {
        return new MealIngredientsAdapter(meal, quantitiesModel);
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
    public MealDetailScreen provideMealDetailScreen() {
        return (MealDetailScreen) fragment.getActivity();
    }

    @Provides
    @PerFragment
    public ImageHolderDelegate provideImageHolder(Picasso picasso,
                                                  PermissionsHelper permissionsHelper,
                                                  Provider<ImageView> imageViewProvider) {
        return new WithoutPlaceholderImageHolderDelegate(picasso, permissionsHelper, imageViewProvider);
    }

    @Provides
    public ImageView provideImageViewProvider() {
        return fragment.getImageView();
    }

    @Provides
    public FragmentActivity provideFragmentActivity() {
        return fragment.getActivity();
    }

}
