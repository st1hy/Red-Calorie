package com.github.st1hy.countthemcalories.activities.overview.meals.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.overview.meals.mealitems.AbstractMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.meals.mealitems.MealInteraction;
import com.github.st1hy.countthemcalories.activities.overview.meals.mealitems.MealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.meals.model.CurrentDayModel;
import com.github.st1hy.countthemcalories.activities.overview.meals.model.MealsViewModel;
import com.github.st1hy.countthemcalories.activities.overview.meals.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.meals.model.commands.MealsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.overview.meals.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailParams;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.command.undo.UndoTransformer;
import com.github.st1hy.countthemcalories.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.overview.meals.mealitems.MealRowComponent;
import com.github.st1hy.countthemcalories.inject.activities.overview.meals.mealitems.MealRowComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.overview.meals.mealitems.MealRowModule;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.overview.meals.mealitems.MealInteraction.ofType;

@PerFragment
public class MealsAdapter extends RecyclerAdapterWrapper<AbstractMealItemHolder>
        implements BasicLifecycle {

    private static final int mealItemLayout = R.layout.overview_item_scrolling;
    private static final int mealItemLayoutBottom = R.layout.list_item_bottom;
    private static final int BOTTOM_PADDING = 1;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @NonNull
    private final RxMealsDatabaseModel databaseModel;
    @NonNull
    private final PhysicalQuantitiesModel quantityModel;
    @NonNull
    private final OverviewView view;
    @NonNull
    private final MealsDatabaseCommands commands;
    @NonNull
    private final MealsViewModel viewModel;
    @NonNull
    private final UndoView undoView;
    @NonNull
    private final MealRowComponentFactory mealRowComponentFactory;

    private List<Meal> list = Collections.emptyList();

    private final PublishSubject<MealInteraction> interactionSubject = PublishSubject.create();

    @Nullable
    private MealItemHolder disabledHolder;
    @Inject
    CurrentDayModel currentDayModel;

    @Inject
    public MealsAdapter(@NonNull OverviewView view,
                        @NonNull RxMealsDatabaseModel databaseModel,
                        @NonNull PhysicalQuantitiesModel quantityModel,
                        @NonNull MealsDatabaseCommands commands,
                        @NonNull MealsViewModel viewModel,
                        @NonNull UndoView undoView,
                        @NonNull MealRowComponentFactory mealRowComponentFactory) {
        this.view = view;
        this.commands = commands;
        this.databaseModel = databaseModel;
        this.quantityModel = quantityModel;
        this.viewModel = viewModel;
        this.undoView = undoView;
        this.mealRowComponentFactory = mealRowComponentFactory;
    }

    @Override
    public void onStart() {
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

    @Override
    public int getItemCount() {
        int listSize = list.size();
        return listSize > 0 ? listSize + BOTTOM_PADDING : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < list.size()) {
            return mealItemLayout;
        } else {
            return mealItemLayoutBottom;
        }
    }

    @Override
    public AbstractMealItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MealRowComponent component = mealRowComponentFactory.newMealRowComponent(
                new MealRowModule(viewType, parent));
        if (viewType == mealItemLayout) {
            MealItemHolder holder = component.getMealHolder();
            holder.fillParent(parent);
            return holder;
        } else {
            return component.getSpaceHolder();
        }
    }

    @Override
    public void onBindViewHolder(AbstractMealItemHolder itemHolder, int position) {
        if (itemHolder instanceof MealItemHolder) {
            MealItemHolder holder = (MealItemHolder) itemHolder;
            final Meal meal = list.get(position);
            holder.setName(meal.getName());
            holder.setMeal(meal);
            holder.setDate(quantityModel.formatTime(meal.getCreationDate()));
            onBindIngredients(holder, meal.getIngredients());
            onBindImage(meal, holder);
            holder.setEnabled(true);
        }
    }

    @Override
    public void onViewAttachedToWindow(AbstractMealItemHolder itemHolder) {
        if (itemHolder instanceof MealItemHolder) {
            MealItemHolder holder = (MealItemHolder) itemHolder;
            holder.onAttached(interactionSubject);
        }
    }

    @Override
    public void onViewDetachedFromWindow(AbstractMealItemHolder itemHolder) {
        if (itemHolder instanceof MealItemHolder) {
            MealItemHolder holder = (MealItemHolder) itemHolder;
            holder.onDetached();
        }
    }

    private void editMealWithId(long mealId) {
        Pair<Integer, Meal> mealPair = getMealPositionWithId(mealId);
        if (mealPair != null) {
            openEditScreen(mealPair.second);
        } else {
            Timber.w("Meal with id: %s no longer exist", mealId);
        }
    }

    private void deleteMealWithId(long mealId) {
        Pair<Integer, Meal> mealPair = getMealPositionWithId(mealId);
        if (mealPair != null) {
            deleteMeal(mealPair.second, mealPair.first);
        } else {
            Timber.w("Meal with id: %s no longer exist", mealId);
        }
    }

    private void openEditScreen(@NonNull Meal meal) {
        view.editMeal(meal);
    }

    private void loadMeals() {
        DateTime currentDay = currentDayModel.getCurrentDay();
        DateTime from = currentDay.withTimeAtStartOfDay();
        DateTime to = currentDay.plusDays(1).withTimeAtStartOfDay();
        subscriptions.add(databaseModel.getAllFilteredSortedDate(from, to)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OnUpdatedDataSet()));
    }

    private void onBindIngredients(@NonNull final MealItemHolder holder, @NonNull List<Ingredient> ingredients) {
        Observable<Ingredient> ingredientObservable = Observable.from(ingredients);
        Observable<BigDecimal> decimalObservable = ingredientObservable
                .map(quantityModel.mapToEnergy()).cache();
        ingredientObservable.map(ingredient -> ingredient.getIngredientTypeOrNull().getName())
                .map(joinStrings())
                .lastOrDefault(new StringBuilder(0))
                .map(stringBuilder -> stringBuilder.toString().trim())
                .subscribe(holder::setIngredients);
        decimalObservable.map(quantityModel.sumAll())
                .lastOrDefault(BigDecimal.ZERO)
                .map(quantityModel.setScale(0))
                .map(quantityModel.energyAsString())
                .subscribe(holder::setTotalEnergy);
    }

    private void onBindImage(@NonNull Meal meal, @NonNull MealItemHolder holder) {
        holder.setImageUri(meal.getImageUri());
    }

    @Nullable
    private Pair<Integer, Meal> getMealPositionWithId(long mealId) {
        List<Meal> meals = this.list;
        for (int i = 0; i < meals.size(); i++) {
            Meal meal = meals.get(i);
            Long id = meal.getId();
            if (id != null && id == mealId) {
                return Pair.create(i, meal);
            }
        }
        return null;
    }

    private void deleteMeal(@NonNull Meal meal, int position) {
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
                                    list.add(position, meal1);
                                    onNewDataSet(list);
                                    notifyItemInserted(position);
                                })
                        ))
                .map(Functions.intoResponse())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    list.remove(position);
                    onNewDataSet(list);
                    notifyItemRemoved(position);
                }));
    }

    private void onNewDataSet(@NonNull List<Meal> meals) {
        this.list = meals;
        setEmptyListVisibility();
    }

    private void setEmptyListVisibility() {
        view.setEmptyListVisibility(list.size() == 0 ? Visibility.VISIBLE : Visibility.GONE);
    }

    @NonNull
    private Func1<? super String, StringBuilder> joinStrings() {
        return new Func1<String, StringBuilder>() {
            StringBuilder builder = new StringBuilder(64);

            @Override
            public StringBuilder call(String s) {
                return builder.append(s).append('\n');
            }
        };
    }


    private class OnUpdatedDataSet extends SimpleSubscriber<List<Meal>> {

        @Override
        public void onNext(List<Meal> meals) {
            onNewDataSet(meals);
            notifyDataSetChanged();
        }

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
}
