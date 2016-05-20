package com.github.st1hy.countthemcalories.activities.mealdetail.presenter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.model.MealDetailModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailView;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.database.Meal;
import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormat;

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
        if (!model.isDataValid()) {
            view.finish();
        } else {
            subscriptions.add(model.getMealObservable().subscribe(onMealLoaded()));
            subscriptions.add(view.getEditObservable().subscribe(onEditClicked()));
            subscriptions.add(view.getDeleteObservable().subscribe(onDeleteClicked()));
            adapter.onStart();
        }
    }

    @Override
    public void onStop() {
        subscriptions.clear();
        adapter.onStop();
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
        view.setDate(DateTimeFormat.shortTime().print(meal.getCreationDate()));
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

    private void bindImage(@NonNull Meal meal) {
        if (!Uri.EMPTY.equals(meal.getImageUri())) {
            subscriptions.add(RxPicasso.Builder.with(picasso, meal.getImageUri())
                    .centerCrop()
                    .fit()
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
                Intent intent = new Intent();
                intent.putExtra(MealDetailActivity.EXTRA_RESULT_MEAL_ID_LONG, model.getMeal().getId());
                view.setResultAndFinish(MealDetailActivity.RESULT_EDIT, intent);
            }
        };
    }

    @NonNull
    private Action1<Void> onDeleteClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent();
                intent.putExtra(MealDetailActivity.EXTRA_RESULT_MEAL_ID_LONG, model.getMeal().getId());
                view.setResultAndFinish(MealDetailActivity.RESULT_DELETE, intent);
            }
        };
    }
}
