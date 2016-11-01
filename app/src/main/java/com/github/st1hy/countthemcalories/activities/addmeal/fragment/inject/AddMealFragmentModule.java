package com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealPresenterImp;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.IngredientsAdapter;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.NewImageHolderDelegate;
import com.google.common.base.Preconditions;
import com.squareup.picasso.Picasso;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.core.FragmentDepends.checkIsSubclass;

@Module
public class AddMealFragmentModule {

    public static final String EXTRA_MEAL_PARCEL = "edit meal parcel";
    public static final String EXTRA_INGREDIENT_PARCEL = "edit ingredient parcel";

    private final AddMealFragment fragment;
    @Nullable
    private final Bundle savedState;

    public AddMealFragmentModule(AddMealFragment fragment, @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    public AddMealPresenter providePresenter(AddMealPresenterImp presenter) {
        return presenter;
    }


    @Provides
    public AddMealView provideView() {
        return fragment;
    }

    @Provides
    public AddMealScreen provideScreen() {
        return checkIsSubclass(fragment.getActivity(), AddMealScreen.class);
    }

    @Provides
    @PerFragment
    public MealIngredientsListModel provideListModel(RxIngredientsDatabaseModel model,
                                                     RxMealsDatabaseModel databaseModel,
                                                     @Nullable MealParcel mealParcel,
                                                     @Nullable IngredientTypeParcel ingredientsParcel,
                                                     @Nullable Bundle savedState) {
        return new MealIngredientsListModel(model, databaseModel, mealParcel, ingredientsParcel,
                savedState);
    }

    @Provides
    @PerFragment
    public IngredientsAdapter provideListAdapter(AddMealView view, MealIngredientsListModel model,
                                                 PhysicalQuantitiesModel namesModel, Picasso picasso,
                                                 PermissionsHelper permissionsHelper) {
        return new IngredientsAdapter(view, model, namesModel, picasso, permissionsHelper);
    }

    @Provides
    public Resources provideResources() {
        return fragment.getResources();
    }

    @Provides
    @Nullable
    @PerFragment
    public MealParcel provideMealParcel() {
        Bundle arguments = fragment.getArguments();
        Preconditions.checkNotNull(arguments);
        return arguments.getParcelable(EXTRA_MEAL_PARCEL);
    }

    @Provides
    @Nullable
    public Bundle provideSavedState() {
        return savedState;
    }

    @Provides
    @Nullable
    public IngredientTypeParcel provideIngredientsParcel() {
        Bundle arguments = fragment.getArguments();
        Preconditions.checkNotNull(arguments);
        IngredientTypeParcel parcel = arguments.getParcelable(EXTRA_INGREDIENT_PARCEL);
        arguments.remove(EXTRA_INGREDIENT_PARCEL);
        return parcel;
    }

    @Provides
    public FragmentActivity provideFragmentActivity() {
        return fragment.getActivity();
    }

    @Provides
    public ImageHolderDelegate provideImageHolderDelegate(Picasso picasso,
                                                          PermissionsHelper permissionsHelper,
                                                          Provider<ImageView> image) {
        return new NewImageHolderDelegate(picasso, permissionsHelper, image);
    }

    @Provides
    public ImageView provideImageView() {
        return fragment.getImageView();
    }
}
