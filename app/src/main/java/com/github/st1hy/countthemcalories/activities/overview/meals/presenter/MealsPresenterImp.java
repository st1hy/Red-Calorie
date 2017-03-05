package com.github.st1hy.countthemcalories.activities.overview.meals.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.activities.overview.mealpager.AddMealController;
import com.github.st1hy.countthemcalories.activities.overview.meals.mealitems.MealInteraction;
import com.github.st1hy.countthemcalories.activities.overview.meals.mealitems.MealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.meals.model.CurrentDayModel;
import com.github.st1hy.countthemcalories.activities.overview.meals.model.MealsViewModel;
import com.github.st1hy.countthemcalories.activities.overview.meals.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.meals.model.commands.MealsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.overview.meals.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailParams;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriodModel;
import com.github.st1hy.countthemcalories.core.command.undo.UndoTransformer;
import com.github.st1hy.countthemcalories.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import org.joda.time.DateTime;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.overview.meals.mealitems.MealInteraction.ofType;

@PerFragment
public class MealsPresenterImp implements MealsPresenter {

    @Inject
    MealsAdapter adapter;
    @Inject
    AddMealController addMealController;
    @Inject
    CurrentDayModel currentDayModel;
    @Inject
    RxMealsDatabaseModel databaseModel;
    @Inject
    OverviewView view;
    @Inject
    MealsDatabaseCommands commands;
    @Inject
    MealsViewModel viewModel;
    @Inject
    UndoView undoView;
    @Inject
    TimePeriodModel timePeriodModel;
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
                interactionSubject.filter(ofType(MealInteraction.Type.OPEN))
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
                                case CANCELED:
                                    break;
                            }
                        })
        );
        subscriptions.add(
                interactionSubject.filter(ofType(MealInteraction.Type.EDIT))
                        .doOnNext(disableViewHolder())
                        .subscribe(interaction -> {
                            MealItemHolder holder = interaction.getHolder();
                            openEditScreen(holder.getMeal());
                        })
        );
        subscriptions.add(
                interactionSubject.filter(ofType(MealInteraction.Type.DELETE))
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
            openEditScreen(mealPair.second);
        } else {
            Timber.w("Meal with id: %s no longer exist", mealId);
        }
    }

    private void deleteMealWithId(long mealId) {
        Pair<Integer, Meal> mealPair = adapter.getMealPositionWithId(mealId);
        if (mealPair != null) {
            deleteMeal(mealPair.second, mealPair.first);
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
                            return databaseModel.getAllFilteredSortedDate(from, to);
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
        subscriptions.add(commands.delete(meal)
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
                                    timePeriodModel.refresh();
                                })
                        ))
                .map(Functions.intoResponse())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    adapter.removeMealAtPosition(positionOnList);
                    setEmptyListVisibility();
                    timePeriodModel.refresh();
                }));
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
