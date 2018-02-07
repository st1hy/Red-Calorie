package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.IngredientsListAdapter;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.addmeal.model.ShowIngredientsInfo;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.core.rx.Filters;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class AddMealPresenterImp implements AddMealPresenter {

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    @Nullable
    private IngredientItemViewHolder disabledItem;

    @Inject
    AddMealView view;
    @Inject
    AddMealModel model;
    @Inject
    MealIngredientsListModel listModel;
    @Inject
    Meal meal;
    @Inject
    SelectPicturePresenter picturePresenter;
    @Inject
    Observable<AddMealMenuAction> menuActionObservable;
    @Inject
    IngredientsListAdapter adapter;
    @Inject
    PhysicalQuantitiesModel quantityModel;
    @Inject
    PublishSubject<IngredientItemViewHolder> ingredientClicks;

    @Inject
    public AddMealPresenterImp() {
    }

    @Override
    public void onStart() {
        picturePresenter.loadImageUri(meal.getImageUri());
        view.setName(meal.getName());
        subscribe(
                view.getNameObservable()
                        .skip(1)
                        .subscribe(charSequence -> meal.setName(charSequence.toString()))
        );
        subscribe(
                menuActionObservable.filter(Filters.equalTo(AddMealMenuAction.SAVE))
                        .map(Functions.INTO_VOID)
                        .filter(aVoid -> validateMeal())
                        .flatMap(aVoid1 -> model.saveToDatabase())
                        .subscribe(view::onMealSaved)
        );
        setupTime();
        onAdapterStart();
    }


    private void onAdapterStart() {
        adapter.notifyDataSetChanged();
        onDataSetChanged();
        subscribe(
                Observable.just(listModel.removeExtraIngredientType())
                        .filter(Functions.NOT_NULL)
                        .mergeWith(
                                view.getAddIngredientButtonObservable()
                                        .compose(view.newIngredients())
                        )
                        .map(ingredient -> ShowIngredientsInfo.of(-1L, ingredient,
                                Collections.emptyList()))
                        .mergeWith(
                                ingredientClicks
                                        .filter(any -> disabledItem == null) //ignore when any change in progress
                                        .map(viewHolder -> {
                                            Ingredient ingredient = viewHolder.getIngredient();
                                            final int position = listModel.indexOf(ingredient);
                                            //ignore item currently changing
                                            if (position != -1) {
                                                disabledItem = viewHolder;
                                                viewHolder.setEnabled(false);
                                            }
                                            final List<Pair<View, String>> sharedViews = ImmutableList.of(
                                                    pairOf(viewHolder.getImage(), "ingredient-shared-view-image")
                                            );
                                            return ShowIngredientsInfo.of(position, ingredient, sharedViews);
                                        })
                                        .filter(info -> info.getId() != -1) //ignore item currently changing
                        )
                        .compose(view.showIngredientDetails())
                        .doOnNext(action -> {
                            if (disabledItem != null) {
                                disabledItem.setEnabled(true);
                                disabledItem = null;
                            }
                        })
                        .subscribe(ingredientAction -> {
                            switch (ingredientAction.getType()) {
                                case EDIT:
                                    onIngredientEditFinished(ingredientAction.getId(),
                                            ingredientAction.getIngredient());
                                    break;
                                case REMOVE:
                                    onIngredientRemoved(ingredientAction.getId());
                                    break;
                                case CANCELED:
                                    break;
                            }
                        })
        );
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }


    private boolean validateMeal() {
        return validateIngredients();
    }

    private boolean validateIngredients() {
        Optional<String> ingredientsError = model.getIngredientsError();
        if (ingredientsError.isPresent()) view.showSnackbarError(ingredientsError.get());
        else view.hideSnackbarError();
        return !ingredientsError.isPresent();
    }

    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }

    private void setupTime() {
        setMealTime(model.getDisplayTime());
        if (meal.getCreationDate() == null) {
            subscribe(
                    Observable.interval(model.getTimeToNextMinuteMils(), 60_000L,
                            TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                            .takeWhile(ignore -> meal.getCreationDate() == null)
                            .map(ignore -> DateTime.now())
                            .subscribe(this::setMealTime)
            );
        }
        subscribe(
                view.mealTimeClicked()
                        .compose(view.openTimePicker(model.getDisplayTime()))
                        .subscribe(selectedTime -> {
                            meal.setCreationDate(selectedTime);
                            setMealTime(selectedTime);
                        })
        );
    }

    private void setMealTime(DateTime now) {
        view.setMealTime(model.formatTime(now));
        view.setHint(model.getMealNameHint());
    }

    private void onDataSetChanged() {
        setEmptyIngredientsVisibility();
        setTotalCalories();
    }

    private void setEmptyIngredientsVisibility() {
        view.setEmptyIngredientsVisibility(Visibility.of(adapter.getItemCount() == 0));
    }

    private void setTotalCalories() {
        Observable.from(listModel.getIngredients())
                .map(quantityModel.mapToEnergy())
                .map(quantityModel.sumAll())
                .lastOrDefault(0.0)
                .map(quantityModel.energyAsString())
                .subscribe(view::setTotalEnergy);
    }


    private void onIngredientRemoved(long requestId) {
        if (requestId == -1L) return;
        listModel.removeIngredient((int) requestId);
        adapter.notifyDataSetChanged();
        onDataSetChanged();
    }

    private void onIngredientEditFinished(long requestId, @NonNull Ingredient ingredient) {
        if (requestId == -1) {
            onIngredientAdded(ingredient);
        } else {
            onIngredientEdited(requestId, ingredient);
        }
    }

    private void onIngredientEdited(long requestId, @NonNull Ingredient ingredient) {
        int position = (int) requestId;
        Ingredient current = listModel.getItemAt(position);
        if (Math.abs(current.getAmount() - ingredient.getAmount()) > 0.001) {
            listModel.modifyIngredient(position, ingredient);
            notifyChanged(position);
        }
    }

    private void onIngredientAdded(@NonNull Ingredient ingredient) {
        Long id = ingredient.getIngredientTypeOrNull().getId();
        Ingredient oldIngredient = listModel.findIngredientByTypeId(id);
        if (oldIngredient != null) {
            double amount = oldIngredient.getAmount() + ingredient.getAmount();
            oldIngredient.setAmount(amount);
            notifyChanged(listModel.indexOf(oldIngredient));
        } else {
            int position = listModel.addIngredient(ingredient);
            notifyInserted(position);
        }
    }

    private void notifyInserted(int position) {
        adapter.notifyItemInserted(position);
        view.scrollTo(position);
        view.hideSnackbarError();
        onDataSetChanged();
    }

    private void notifyChanged(int position) {
        adapter.notifyItemChanged(position);
        onDataSetChanged();
    }

    @NonNull
    private static Pair<View, String> pairOf(@NonNull View view, @NonNull String string) {
        return Pair.create(view, string);
    }
}
