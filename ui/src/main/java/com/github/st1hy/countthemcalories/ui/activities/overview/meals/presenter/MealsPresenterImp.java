package com.github.st1hy.countthemcalories.ui.activities.overview.meals.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.overview.mealpager.AddMealController;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.MealInteraction;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.MealsAdapter;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.holder.MealItemHolder;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.model.CurrentDayModel;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.model.MealsViewModel;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.view.OverviewView;
import com.github.st1hy.countthemcalories.ui.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.ui.activities.overview.model.MealDetailParams;
import com.github.st1hy.countthemcalories.ui.contract.Meal;
import com.github.st1hy.countthemcalories.ui.contract.MealsRepo;
import com.github.st1hy.countthemcalories.ui.contract.MealStatisticRepo;
import com.github.st1hy.countthemcalories.ui.core.command.undo.UndoTransformer;
import com.github.st1hy.countthemcalories.ui.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.ui.core.rx.Functions;
import com.github.st1hy.countthemcalories.ui.core.state.Visibility;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

import org.joda.time.DateTime;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

@PerFragment
public class MealsPresenterImp implements MealsPresenter {

    @Inject
    MealsAdapter adapter;
    @Inject
    AddMealController addMealController;
    @Inject
    CurrentDayModel currentDayModel;
    @Inject
    MealsRepo mealsRepo;
    @Inject
    OverviewView view;
    @Inject
    MealsViewModel viewModel;
    @Inject
    UndoView undoView;
    @Inject
    MealStatisticRepo mealStatisticRepo;
    @Inject
    PublishSubject<MealInteraction> interactionSubject;

    @Nullable
    private MealItemHolder disabledHolder;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    MealsPresenterImp() {
    }

    @Override
    public void onStart() {
        view.setEmptyBackground(currentDayModel.getCurrentPosition() % 2 == 0 ?
                R.drawable.ic_empty_left :
                R.drawable.ic_empty_right);
        onStartAdapter();
        subscriptions.add(
                currentDayModel.getCurrentDay()
                        .switchMap(
                                date -> addMealController.getAddNewMealObservable(date)
                                        .map(any -> date)
                        )
                        .doOnNext(any -> addMealController.closeFloatingMenu())
                        .subscribe(date -> addMealController.addNewMeal(date))
        );
    }

    private void onStartAdapter() {
        loadMeals();
        subscriptions.add(
                interactionSubject.filter(MealInteraction.ofType(MealInteraction.Type.OPEN))
                        .doOnNext(disableViewHolder())
                        .map(interaction -> {
                            MealItemHolder holder = interaction.getHolder();
                            return new MealDetailParams(holder.getMeal(), holder.getImage());
                        })
                        .compose(view.openMealDetails())
                        .doOnNext(enableViewHolder())
                        .subscribe(mealDetailAction -> {
                            long mealId = mealDetailAction.getId();
                            switch (mealDetailAction.getType()) {
                                case DELETE:
                                    deleteMealWithId(mealId);
                                    break;
                                case EDIT:
                                    editMealWithId(mealId);
                                    break;
                                case COPY:
                                    copyMealWithId(mealId);
                                    break;
                                case CANCELED:
                                    break;
                            }
                        })
        );
        subscriptions.add(
                interactionSubject.filter(MealInteraction.ofType(MealInteraction.Type.EDIT))
                        .doOnNext(disableViewHolder())
                        .subscribe(interaction -> {
                            MealItemHolder holder = interaction.getHolder();
                            openEditScreen(holder.getMeal());
                        })
        );
        subscriptions.add(
                interactionSubject.filter(MealInteraction.ofType(MealInteraction.Type.DELETE))
                        .doOnNext(disableViewHolder())
                        .subscribe(interaction -> {
                            long id = interaction.getHolder().getMeal().getId();
                            deleteMealWithId(id);
                        })
        );
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    private void editMealWithId(long mealId) {
        Pair<Integer, Meal> mealPair = adapter.getMealPositionWithId(mealId);
        if (mealPair != null) {
            openEditScreen(checkNotNull(mealPair.second));
        } else {
            Timber.w("Meal with id: %s no longer exist", mealId);
        }
    }

    private void deleteMealWithId(long mealId) {
        Pair<Integer, Meal> mealPair = adapter.getMealPositionWithId(mealId);
        if (mealPair != null) {
            deleteMeal(checkNotNull(mealPair.second), checkNotNull(mealPair.first));
        } else {
            Timber.w("Meal with id: %s no longer exist", mealId);
        }
    }

    private void copyMealWithId(long mealId) {
        Pair<Integer, Meal> mealPair = adapter.getMealPositionWithId(mealId);
        if (mealPair != null) {
            copyMeal(mealPair.second);
        } else {
            Timber.w("Meal with id: %s no longer exist", mealId);
        }

    }

    private void loadMeals() {
        subscriptions.add(
                currentDayModel.getCurrentDay()
                        .switchMap(date -> {
                            DateTime from = date.withTimeAtStartOfDay();
                            DateTime to = date.plusDays(1).withTimeAtStartOfDay();
                            return mealsRepo.getAllFilteredSortedDate(from, to);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(meals -> {
                            adapter.onNewDataSet(meals);
                            setEmptyListVisibility();
                            adapter.notifyDataSetChanged();
                        })
        );
    }

    private void openEditScreen(@NonNull Meal meal) {
        view.editMeal(meal);
    }

    private void deleteMeal(@NonNull Meal meal, int positionOnList) {
        subscriptions.add(mealsRepo.delete(meal)
                .doOnNext(deleteResponse ->
                        subscriptions.add(deleteResponse.undoAvailability()
                                .compose(new UndoTransformer<>(deleteResponse,
                                        isAvailable -> {
                                            if (isAvailable)
                                                return undoView.showUndoMessage(
                                                        viewModel.getUndoRemoveMealMessage());
                                            else {
                                                undoView.hideUndoMessage();
                                                return Observable.empty();
                                            }
                                        }))
                                .subscribe(meal1 -> {
                                    adapter.addMealAtPosition(meal1, positionOnList);
                                    setEmptyListVisibility();
                                    mealStatisticRepo.refresh();
                                })
                        ))
                .map(Functions.intoResponse())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    adapter.removeMealAtPosition(positionOnList);
                    setEmptyListVisibility();
                    mealStatisticRepo.refresh();
                }));
    }


    private void copyMeal(Meal meal) {
        view.copyMeal(meal);
    }


    @NonNull
    private Action1<MealInteraction> disableViewHolder() {
        return interaction -> {
            disabledHolder = interaction.getHolder();
            disabledHolder.setEnabled(false);
        };
    }

    @NonNull
    private Action1<MealDetailAction> enableViewHolder() {
        return mealDetailAction -> {
            if (disabledHolder != null) {
                disabledHolder.setEnabled(true);
                disabledHolder = null;
            }
        };
    }

    private void setEmptyListVisibility() {
        view.setEmptyListVisibility(adapter.getMealsCount() == 0 ? Visibility.VISIBLE : Visibility.GONE);
    }

}
