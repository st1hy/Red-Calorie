package com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.IngredientsListAdapter;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.inject.IngredientListComponentFactory;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealPresenterImp;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealViewController;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.core.headerpicture.PictureModel;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenterImp;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.core.DefaultMealNameModule;
import com.github.st1hy.countthemcalories.inject.core.DialogModule;
import com.github.st1hy.countthemcalories.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.inject.core.headerpicture.PictureModule;
import com.github.st1hy.countthemcalories.inject.quantifier.bundle.FragmentArguments;
import com.github.st1hy.countthemcalories.inject.quantifier.bundle.FragmentSavedState;
import com.github.st1hy.countthemcalories.inject.quantifier.context.ActivityContext;
import com.github.st1hy.countthemcalories.inject.quantifier.datetime.NewMealDate;
import com.github.st1hy.countthemcalories.inject.quantifier.view.FragmentRootView;

import org.joda.time.DateTime;
import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.subjects.PublishSubject;

import static org.parceler.Parcels.unwrap;

@Module(includes = {
        PermissionModule.class,
        DialogModule.class,
        DefaultMealNameModule.class
})
public abstract class AddMealFragmentModule {

    public static final String EXTRA_MEAL_PARCEL = "edit meal parcel";
    public static final String EXTRA_INGREDIENT_PARCEL = "edit ingredient parcel";
    public static final String EXTRA_NEW_MEAL_DATE = "new meal date parcel";

    @Binds
    public abstract AddMealPresenter providePresenter(AddMealPresenterImp presenter);

    @Binds
    public abstract AddMealView provideView(AddMealViewController addMealViewController);

    @Binds
    public abstract RecyclerView.Adapter ingredientsAdapter(IngredientsListAdapter listPresenter);

    @Binds
    @PerFragment
    public abstract SelectPicturePresenter picturePresenter(SelectPicturePresenterImp presenter);

    @Binds
    public abstract PictureModel pictureModel(AddMealModel model);

    @Binds
    public abstract IngredientListComponentFactory ingredientListComponentFactory(
            AddMealFragmentComponent component);


    @Provides
    @PerFragment
    public static MealIngredientsListModel provideListModel(
            @Nullable @FragmentSavedState Bundle savedState,
            @NonNull Meal meal,
            @Nullable Ingredient extraIngredient) {

        if (savedState != null) {
            MealIngredientsListModel listModel = Parcels.unwrap(
                    savedState.getParcelable(MealIngredientsListModel.SAVED_INGREDIENTS));
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
    public static Meal provideMeal(@Nullable @FragmentSavedState Bundle savedState,
                                   @FragmentArguments Bundle arguments) {
        if (savedState != null) {
            return unwrap(savedState.getParcelable(AddMealModel.SAVED_MEAL_STATE));
        } else {
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
    public static IngredientTemplate provideExtraIngredientTemplate(@FragmentArguments Bundle arguments) {
        IngredientTemplate ingredientTemplate = unwrap(arguments.getParcelable(EXTRA_INGREDIENT_PARCEL));
        arguments.remove(EXTRA_INGREDIENT_PARCEL);
        return ingredientTemplate;
    }

    @Provides
    @Nullable
    public static Ingredient extraIngredient(@Nullable IngredientTemplate extraTemplate) {
        if (extraTemplate != null) {
            return new Ingredient(extraTemplate, BigDecimal.ZERO);
        } else {
            return null;
        }
    }

    @Provides
    public static Observable<AddMealMenuAction> menuActionObservable(
            PublishSubject<AddMealMenuAction> actionPublishSubject) {
        return actionPublishSubject.asObservable();
    }

    @Provides
    public static RecyclerView recyclerView(@FragmentRootView View rootView,
                                            RecyclerView.Adapter adapter,
                                            @ActivityContext Context context) {

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(
                R.id.add_meal_ingredients_list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        return recyclerView;
    }

    @Provides
    @Nullable
    @NewMealDate
    public static DateTime newMealDate(@FragmentArguments Bundle arguments) {
        return (DateTime) arguments.getSerializable(EXTRA_NEW_MEAL_DATE);
    }

    @Provides
    @PerFragment
    public static PublishSubject<IngredientItemViewHolder> ingredientClicks() {
        return PublishSubject.create();
    }

}
