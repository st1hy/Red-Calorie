package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.model.MealDetailModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailView;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.database.Meal;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MealDetailPresenterImpl implements MealDetailPresenter {

    final MealDetailView view;
    final MealDetailModel model;
    final Picasso picasso;
    final MealIngredientsAdapter adapter;
    final PhysicalQuantitiesModel quantitiesModel;
    final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public MealDetailPresenterImpl(@NonNull MealDetailView view, @NonNull MealDetailModel model,
                                   @NonNull Picasso picasso, @NonNull MealIngredientsAdapter adapter,
                                   @NonNull PhysicalQuantitiesModel quantitiesModel) {
        this.view = view;
        this.model = model;
        this.picasso = picasso;
        this.adapter = adapter;
        this.quantitiesModel = quantitiesModel;
    }

    @Override
    public void onStart() {
        subscriptions.add(model.getMealObservable().subscribe(onMealLoaded()));
        subscriptions.add(view.getEditObservable().subscribe(onEditClicked()));
        subscriptions.add(view.getDeleteObservable().subscribe(onDeleteClicked()));
        adapter.onStart();
    }

    @Override
    public void onStop() {
        subscriptions.clear();
        adapter.onStop();
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        model.onSaveState(outState);
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
        Uri imageUri = meal.getImageUri();
        if (imageUri != null && !imageUri.equals(Uri.EMPTY)) {
            subscriptions.add(RxPicasso.Builder.with(picasso, imageUri)
                    .centerCrop()
                    .fit()
                    .noFade()
                    .into(view.getImageView())
                    .asObservable()
                    .subscribe());

        } else {
            view.getImageView().setImageResource(R.drawable.ic_fork_and_knife_wide);
        }
    }

    private Action1<Void> onEditClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.editMealWithId(model.getMeal().getId());
            }
        };
    }

    @NonNull
    private Action1<Void> onDeleteClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.deleteMealWithId(model.getMeal().getId());
            }
        };
    }
}
