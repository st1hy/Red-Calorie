package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.model.MealDetailModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealDetailPresenter;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealDetailPresenterImpl;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealIngredientsAdapter;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailFragment;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailView;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailScreen;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;
import com.squareup.picasso.Picasso;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

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
    public MealDetailModel provideModel(@NonNull RxMealsDatabaseModel databaseModel,
                                        @Nullable MealParcel mealSource,
                                        @Nullable Bundle savedState) {
        return new MealDetailModel(databaseModel, mealSource, savedState);
    }

    @Provides
    @PerFragment
    public MealIngredientsAdapter provideAdapter(MealDetailModel model,
                                                 PhysicalQuantitiesModel quantitiesModel) {
        return new MealIngredientsAdapter(model, quantitiesModel);
    }

    @Provides
    public MealDetailPresenter providePresenter(MealDetailPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    public MealParcel provideMealParcel() {
        return fragment.getArguments().getParcelable(MealDetailFragment.ARG_MEAL_PARCEL);
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
        return new ImageHolderDelegate(picasso, permissionsHelper, imageViewProvider) {
            @NonNull
            @Override
            protected Observable<RxPicasso.PicassoEvent> loadImage(@NonNull Uri uri) {
                return RxPicasso.Builder.with(picasso, uri)
                        .centerCrop()
                        .fit()
                        .into(imageViewProvider.get())
                        .asObservable();
            }
        };
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
