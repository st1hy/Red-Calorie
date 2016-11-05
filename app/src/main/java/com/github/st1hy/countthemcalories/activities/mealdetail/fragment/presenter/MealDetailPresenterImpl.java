package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailView;
import com.github.st1hy.countthemcalories.core.picture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.Meal;

import org.parceler.Parcels;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.picture.imageholder.ImageHolderDelegate.from;

public class MealDetailPresenterImpl implements MealDetailPresenter {

    public static final String SAVED_MEAL_STATE = "meal details model";

    private final Meal meal;
    private final MealDetailView view;
    private final MealIngredientsAdapter adapter;
    private final PhysicalQuantitiesModel quantitiesModel;
    private final ImageHolderDelegate imageHolderDelegate;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public MealDetailPresenterImpl(@NonNull MealDetailView view,
                                   @NonNull Meal meal,
                                   @NonNull MealIngredientsAdapter adapter,
                                   @NonNull PhysicalQuantitiesModel quantitiesModel,
                                   @NonNull ImageHolderDelegate imageHolderDelegate) {
        this.view = view;
        this.meal = meal;
        this.adapter = adapter;
        this.quantitiesModel = quantitiesModel;
        this.imageHolderDelegate = imageHolderDelegate;
    }

    @Override
    public void onStart() {
        imageHolderDelegate.onAttached();
        setupView(meal);
        subscriptions.add(view.getEditObservable().subscribe(onEditClicked()));
        subscriptions.add(view.getDeleteObservable().subscribe(onDeleteClicked()));
        adapter.onStart();
    }

    @Override
    public void onStop() {
        imageHolderDelegate.onDetached();
        subscriptions.clear();
        adapter.onStop();
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(SAVED_MEAL_STATE, Parcels.wrap(meal));
    }

    @NonNull
    private Action1<Meal> onMealLoaded() {
        return new Action1<Meal>() {
            @Override
            public void call(Meal meal) {
                setupView(meal);
            }
        };
    }

    private void setupView(@NonNull Meal meal) {
        view.setName(meal.getName());
        bindImage(meal);
        view.setDate(quantitiesModel.formatTime(meal.getCreationDate()));
        Observable.from(meal.getIngredients())
                .map(quantitiesModel.mapToEnergy())
                .map(quantitiesModel.sumAll())
                .lastOrDefault(BigDecimal.ZERO)
                .map(quantitiesModel.energyAsString())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String energy) {
                        view.setEnergy(energy);
                    }
                });
    }

    void bindImage(@NonNull Meal meal) {
        imageHolderDelegate.displayImage(from(meal.getImageUri()));
    }

    private Action1<Void> onEditClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.editMealWithId(meal.getId());
            }
        };
    }

    @NonNull
    private Action1<Void> onDeleteClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.deleteMealWithId(meal.getId());
            }
        };
    }
}
