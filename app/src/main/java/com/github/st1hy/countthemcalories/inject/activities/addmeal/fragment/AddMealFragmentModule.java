package com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.overview.quantifier.datetime.NewMealDate;
import com.github.st1hy.countthemcalories.inject.core.DefaultMealNameModule;

import org.joda.time.DateTime;
import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.subjects.PublishSubject;

import static org.parceler.Parcels.unwrap;

@Module(includes = {
        AddMealFragmentBindings.class,
        DefaultMealNameModule.class
})
public class AddMealFragmentModule {

    public static final String EXTRA_MEAL_PARCEL = "edit meal parcel";
    public static final String EXTRA_INGREDIENT_PARCEL = "edit ingredient parcel";
    public static final String EXTRA_NEW_MEAL_DATE = "new meal date parcel";

    private final AddMealFragment fragment;
    @Nullable
    private final Bundle savedState;

    public AddMealFragmentModule(AddMealFragment fragment, @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    @Named("savedState")
    @Nullable
    public Bundle getSavedState() {
        return savedState;
    }

    @Named("fragmentRootView")
    @Provides
    public View rootView() {
        return fragment.getView();
    }

    @Provides
    @Named("arguments")
    public Bundle arguments() {
        return fragment.getArguments();
    }

    @Provides
    @PerFragment
    public static MealIngredientsListModel provideListModel(
            @Nullable @Named("savedState") Bundle savedState,
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
    public static Meal provideMeal(@Nullable @Named("savedState") Bundle savedState,
                                   @Named("arguments") Bundle arguments) {
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
    public static IngredientTemplate provideExtraIngredientTemplate(@Named("arguments") Bundle arguments) {
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
    public static RecyclerView recyclerView(@Named("fragmentRootView") View rootView,
                                     RecyclerViewAdapterDelegate adapter,
                                     @Named("activityContext") Context context) {

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
    public static DateTime newMealDate(@Named("arguments") Bundle arguments) {
        return (DateTime) arguments.getSerializable(EXTRA_NEW_MEAL_DATE);
    }

}
