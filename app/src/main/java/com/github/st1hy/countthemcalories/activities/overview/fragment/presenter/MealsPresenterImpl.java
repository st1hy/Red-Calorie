package com.github.st1hy.countthemcalories.activities.overview.fragment.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems.MealRowComponent;
import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems.MealRowComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems.MealRowModule;
import com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.AbstractMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.MealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.MealsViewModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.commands.MealsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerViewNotifier;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.command.undo.UndoAction;
import com.github.st1hy.countthemcalories.core.command.undo.UndoTransformer;
import com.github.st1hy.countthemcalories.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate.from;

@PerFragment
public class MealsPresenterImpl implements MealsPresenter {

    private static final int mealItemLayout = R.layout.overview_item_scrolling;
    private static final int bottomSpaceLayout = R.layout.overview_item_bottom_space;

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
    private RecyclerViewNotifier notifier;

    @Inject
    public MealsPresenterImpl(@NonNull OverviewView view,
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
    public void setNotifier(@NonNull RecyclerViewNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void onStart() {
        loadTodayMeals();
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getDaoItemCount()) {
            return mealItemLayout;
        } else {
            return bottomSpaceLayout;
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public AbstractMealItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MealRowComponent component = mealRowComponentFactory.newMealRowComponent(new MealRowModule(viewType, parent, this));
        if (viewType == mealItemLayout) {
            return component.getHolder();
        } else {
            return component.getEmptySpace();
        }
    }

    @Override
    public void onBindViewHolder(AbstractMealItemHolder holder, int position) {
        if (holder instanceof MealItemHolder) {
            onBindMealItemHolder((MealItemHolder) holder, position);
        }
    }


    @Override
    public void onMealClicked(@NonNull final MealItemHolder holder) {
        holder.setEnabled(false);
        view.openMealDetails(holder.getMeal(), holder.getImage())
                .first()
                .doOnNext(new Action1<MealDetailAction>() {
                    @Override
                    public void call(MealDetailAction mealDetailAction) {
                        holder.setEnabled(true);
                    }
                }).subscribe(onDetailScreenAction());
    }

    @Override
    public void onDeleteClicked(@NonNull MealItemHolder holder) {
        holder.setEnabled(false);
        deleteMealWithId(holder.getMeal().getId());
    }

    @Override
    public void onEditClicked(@NonNull MealItemHolder holder) {
        holder.setEnabled(false);
        openEditScreen(holder.getMeal());
    }

    @Override
    public void onViewAttachedToWindow(AbstractMealItemHolder holder) {
        holder.onAttached();
    }

    @Override
    public void onViewDetachedFromWindow(AbstractMealItemHolder holder) {
        holder.onDetached();
    }

    @NonNull
    private Action1<MealDetailAction> onDetailScreenAction() {
        return new Action1<MealDetailAction>() {
            @Override
            public void call(MealDetailAction mealDetailAction) {
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
            }
        };
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

    private void loadTodayMeals() {
        DateTime now = DateTime.now();
        DateTime from = now.withTime(0, 0, 0, 0);
        DateTime to = now.withTime(23, 59, 59, 999);
        subscriptions.add(databaseModel.getAllFilteredSortedDate(from, to)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OnUpdatedDataSet()));
    }

    private int getDaoItemCount() {
        return list.size();
    }

    private void onBindMealItemHolder(@NonNull MealItemHolder holder, int position) {
        final Meal meal = list.get(position);
        holder.setName(meal.getName());
        holder.setMeal(meal);
        holder.setDate(quantityModel.formatTime(meal.getCreationDate()));
        onBindIngredients(holder, meal.getIngredients());
        onBindImage(meal, holder);
        holder.setEnabled(true);
    }

    private void onBindIngredients(@NonNull final MealItemHolder holder, @NonNull List<Ingredient> ingredients) {
        Observable<Ingredient> ingredientObservable = Observable.from(ingredients);
        Observable<BigDecimal> decimalObservable = ingredientObservable
                .map(quantityModel.mapToEnergy()).cache();
        ingredientObservable.map(toNames())
                .map(joinStrings())
                .lastOrDefault(new StringBuilder(0))
                .subscribe(new Action1<StringBuilder>() {
                    @Override
                    public void call(StringBuilder stringBuilder) {
                        holder.setIngredients(stringBuilder.toString().trim());
                    }
                });
        decimalObservable.map(quantityModel.sumAll())
                .lastOrDefault(BigDecimal.ZERO)
                .map(quantityModel.setScale(0))
                .map(quantityModel.energyAsString())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String totalEnergy) {
                        holder.setTotalEnergy(totalEnergy);
                    }
                });
    }

    private void onBindImage(@NonNull Meal meal, @NonNull MealItemHolder holder) {
        holder.setImageUri(from(meal.getImageUri()));
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
                .doOnNext(showUndoRemoval(position))
                .map(Functions.<Void>intoResponse())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onMealDeleted(position)));
    }

    @NonNull
    private Action1<Void> onMealDeleted(final int mealPos) {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                list.remove(mealPos);
                onNewDataSet(list);
                notifier.notifyItemRemoved(mealPos);
            }
        };
    }

    private void onNewDataSet(@NonNull List<Meal> meals) {
        this.list = meals;
        showTotal();
        setEmptyListVisibility();
    }

    private void setEmptyListVisibility() {
        view.setEmptyListVisibility(list.size() == 0 ? Visibility.VISIBLE : Visibility.GONE);
    }

    private void showTotal() {
        subscriptions.add(Observable.from(list)
                .subscribeOn(Schedulers.computation())
                .flatMap(new Func1<Meal, Observable<Ingredient>>() {
                    @Override
                    public Observable<Ingredient> call(Meal meal) {
                        return Observable.from(meal.getIngredients());
                    }
                })
                .map(quantityModel.mapToEnergy())
                .map(quantityModel.sumAll())
                .lastOrDefault(BigDecimal.ZERO)
                .map(quantityModel.setScale(0))
                .map(quantityModel.energyAsString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String energy) {
                        view.setTotalEnergy(energy);
                    }
                }));

    }

    @NonNull
    private Func1<Ingredient, String> toNames() {
        return new Func1<Ingredient, String>() {
            @Override
            public String call(Ingredient ingredient) {
                return ingredient.getIngredientTypeOrNull().getName();
            }
        };
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


    @NonNull
    private Action1<CommandResponse<Void, Meal>> showUndoRemoval(final int position) {
        return new Action1<CommandResponse<Void, Meal>>() {
            @Override
            public void call(final CommandResponse<Void, Meal> deleteResponse) {
                subscriptions.add(deleteResponse.undoAvailability()
                        .compose(onUndoAvailable(deleteResponse, viewModel.getUndoRemoveMealMessage()))
                        .subscribe(onMealAdded(position))
                );
            }
        };
    }

    @NonNull
    private <Response, UndoResponse> Observable.Transformer<Boolean, UndoResponse> onUndoAvailable(
            @NonNull final CommandResponse<Response, UndoResponse> response,
            @StringRes final int undoMessage) {
        return new UndoTransformer<>(response, showUndoMessage(undoMessage));
    }

    @NonNull
    Func1<Boolean, Observable<UndoAction>> showUndoMessage(@StringRes final int undoMessage) {
        return new Func1<Boolean, Observable<UndoAction>>() {
            @Override
            public Observable<UndoAction> call(Boolean isAvailable) {
                if (isAvailable)
                    return undoView.showUndoMessage(undoMessage);
                else {
                    undoView.hideUndoMessage();
                    return Observable.empty();
                }
            }
        };
    }

    @NonNull
    private SimpleSubscriber<Meal> onMealAdded(final int position) {
        return new SimpleSubscriber<Meal>() {
            @Override
            public void onNext(Meal meal) {
                list.add(position, meal);
                onNewDataSet(list);
                notifier.notifyItemInserted(position);
            }
        };
    }

    private class OnUpdatedDataSet extends SimpleSubscriber<List<Meal>> {

        @Override
        public void onNext(List<Meal> meals) {
            onNewDataSet(meals);
            notifier.notifyDataSetChanged();
        }

    }
}
