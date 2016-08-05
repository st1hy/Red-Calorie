package com.github.st1hy.countthemcalories.activities.overview.fragment.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.MealsViewModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.commands.MealsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.fragment.viewholder.AbstractMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.fragment.viewholder.EmptyMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.fragment.viewholder.MealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.command.UndoTransformer;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import dagger.internal.Preconditions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate.from;

public class MealsAdapter extends RecyclerView.Adapter<AbstractMealItemHolder> implements MealItemHolder.Callback {
    static final int mealItemLayout = R.layout.overview_item_scrolling;
    static final int bottomSpaceLayout = R.layout.overview_item_bottom_space;

    final CompositeSubscription subscriptions = new CompositeSubscription();

    final RxMealsDatabaseModel databaseModel;
    final Picasso picasso;
    final PhysicalQuantitiesModel quantityModel;
    final OverviewView view;
    final MealsDatabaseCommands commands;
    final MealsViewModel viewModel;
    final PermissionsHelper permissionsHelper;

    List<Meal> list = Collections.emptyList();

    public MealsAdapter(@NonNull OverviewView view,
                        @NonNull RxMealsDatabaseModel databaseModel,
                        @NonNull Picasso picasso,
                        @NonNull PhysicalQuantitiesModel quantityModel,
                        @NonNull MealsDatabaseCommands commands,
                        @NonNull MealsViewModel viewModel,
                        @NonNull PermissionsHelper permissionsHelper) {
        this.view = view;
        this.commands = commands;
        this.databaseModel = databaseModel;
        this.picasso = picasso;
        this.quantityModel = quantityModel;
        this.viewModel = viewModel;
        this.permissionsHelper = permissionsHelper;
    }

    public void onStart() {
        loadTodayMeals();
        subscriptions.add(view.getDetailScreenActionObservable()
                .subscribe(onDetailScreenAction()));
    }

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
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        Preconditions.checkNotNull(view);
        if (viewType == mealItemLayout) {
            MealItemHolder viewHolder = new MealItemHolder(view, this, picasso, permissionsHelper);
            viewHolder.fillParent(parent);
            return viewHolder;
        } else {
            return new EmptyMealItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(AbstractMealItemHolder holder, int position) {
        if (holder instanceof MealItemHolder) {
            onBindMealItemHolder((MealItemHolder) holder, position);
        }
    }

    @Override
    public void onViewAttachedToWindow(AbstractMealItemHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof MealItemHolder) {
            ((MealItemHolder) holder).onAttached();
        }
    }

    @Override
    public void onViewDetachedFromWindow(AbstractMealItemHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof MealItemHolder) {
            ((MealItemHolder) holder).onDetached();
        }
    }

    @Override
    public void onMealClicked(@NonNull final MealItemHolder holder) {
        holder.setEnabled(false);
        enableAfterMealDetailReturns(holder);
        view.openMealDetails(new MealParcel(holder.getMeal()), holder.getImage());
    }

    private void enableAfterMealDetailReturns(@NonNull final MealItemHolder holder) {
        subscriptions.add(view.getDetailScreenActionObservable()
                .first()
                .subscribe(new Action1<MealDetailAction>() {
                    @Override
                    public void call(MealDetailAction mealDetailAction) {
                        holder.setEnabled(true);
                    }
                }));
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

    @NonNull
    private Action1<MealDetailAction> onDetailScreenAction() {
        return new Action1<MealDetailAction>() {
            @Override
            public void call(MealDetailAction mealDetailAction) {
                long mealId = mealDetailAction.getId();
                switch (mealDetailAction.getType())  {
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

    void editMealWithId(long mealId) {
        Pair<Integer, Meal> mealPair = getMealPositionWithId(mealId);
        if (mealPair != null) {
            openEditScreen(mealPair.second);
        } else {
            Timber.w("Meal with id: %s no longer exist", mealId);
        }
    }

    void deleteMealWithId(long mealId) {
        Pair<Integer, Meal> mealPair = getMealPositionWithId(mealId);
        if (mealPair != null) {
            deleteMeal(mealPair.second, mealPair.first);
        } else {
            Timber.w("Meal with id: %s no longer exist", mealId);
        }
    }

    private void openEditScreen(@NonNull Meal meal) {
        view.openEditMealScreen(new MealParcel(meal));
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

    void onBindImage(@NonNull Meal meal, @NonNull MealItemHolder holder) {
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
                notifyItemRemoved(mealPos);
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
                return ingredient.getIngredientType().getName();
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
    Func1<Boolean, Observable<Void>> showUndoMessage(@StringRes final int undoMessage) {
        return new Func1<Boolean, Observable<Void>>() {
            @Override
            public Observable<Void> call(Boolean isAvailable) {
                if (isAvailable)
                    return view.showUndoMessage(undoMessage);
                else {
                    view.hideUndoMessage();
                    return Observable.empty();
                }
            }
        };
    }

    @NonNull
    SimpleSubscriber<Meal> onMealAdded(final int position) {
        return new SimpleSubscriber<Meal>() {
            @Override
            public void onNext(Meal meal) {
                list.add(position, meal);
                onNewDataSet(list);
                notifyItemInserted(position);
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
}
