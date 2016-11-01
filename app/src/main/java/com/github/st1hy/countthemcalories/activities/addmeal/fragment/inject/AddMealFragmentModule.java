package com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealPresenterImp;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.IngredientsAdapter;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.NewImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.google.common.base.Preconditions;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.core.FragmentDepends.checkIsSubclass;
import static org.parceler.Parcels.unwrap;

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
    public MealIngredientsListModel provideListModel(@NonNull Meal meal, @Nullable IngredientTemplate extraIngredient) {

        if (savedState != null) {
            MealIngredientsListModel listModel = Parcels.unwrap(savedState.getParcelable(MealIngredientsListModel.SAVED_INGREDIENTS));
            listModel.setExtraIngredient(extraIngredient);
            return listModel;
        } else {
            List<Ingredient> ingredients;
            if (meal.hasIngredients()) {
                ingredients = meal.getIngredients();
            } else {
                ingredients = new ArrayList<>(5);
            }
            return new MealIngredientsListModel(ingredients, extraIngredient);
        }
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
    @PerFragment
    public Meal provideMeal() {
        if (savedState != null) {
            return unwrap(savedState.getParcelable(AddMealModel.SAVED_MEAL_STATE));
        } else {
            Bundle arguments = fragment.getArguments();
            Preconditions.checkNotNull(arguments);
            Meal editedMeal = Parcels.unwrap(arguments.getParcelable(EXTRA_MEAL_PARCEL));
            if (editedMeal != null) {
                return editedMeal;
            } else {
                editedMeal = new Meal();
                editedMeal.setName("");
                editedMeal.setImageUri(Uri.EMPTY);
                return editedMeal;
            }
        }
    }

    @Provides
    @Nullable
    public IngredientTemplate provideExtraIngredientTemplate() {
        Bundle arguments = fragment.getArguments();
        Preconditions.checkNotNull(arguments);
        IngredientTemplate ingredientTemplate = unwrap(arguments.getParcelable(EXTRA_INGREDIENT_PARCEL));
        arguments.remove(EXTRA_INGREDIENT_PARCEL);
        return ingredientTemplate;
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
