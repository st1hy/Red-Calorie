package com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.inject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.adapter.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.adapter.IngredientsListAdapter;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.adapter.inject.IngredientListComponentFactory;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.presenter.AddMealPresenterImp;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.view.AddMealViewController;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogModule;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.PictureModel;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.SelectPicturePresenterImp;
import com.github.st1hy.countthemcalories.ui.inject.core.DefaultMealNameModule;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.bundle.FragmentSavedState;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.subjects.PublishSubject;

@Module(includes = {
        DialogModule.class,
        DefaultMealNameModule.class,
        AddMealArgumentsModule.class
})
public abstract class AddMealFragmentModule {

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
    public static Observable<AddMealMenuAction> menuActionObservable(
            PublishSubject<AddMealMenuAction> actionPublishSubject) {
        return actionPublishSubject.asObservable();
    }

    @Provides
    public static RecyclerView recyclerView(@FragmentRootView View rootView,
                                            RecyclerView.Adapter adapter,
                                            @ActivityContext Context context) {

        RecyclerView recyclerView = rootView.findViewById(
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
    @PerFragment
    public static PublishSubject<IngredientItemViewHolder> ingredientClicks() {
        return PublishSubject.create();
    }

}
