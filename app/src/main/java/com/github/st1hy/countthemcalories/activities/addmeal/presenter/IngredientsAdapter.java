package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.model.UnitNamesModel;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.viewholder.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import dagger.internal.Preconditions;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientItemViewHolder> implements
        IngredientItemViewHolder.Callback {
    private final AddMealView view;
    private final MealIngredientsListModel model;
    private final UnitNamesModel namesModel;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public IngredientsAdapter(@NonNull AddMealView view, @NonNull MealIngredientsListModel model,
                              @NonNull UnitNamesModel namesModel) {
        this.view = view;
        this.model = model;
        this.namesModel = namesModel;
    }

    public void onStart() {
        final int initialSize = getItemCount();
        setEmptyViewVisibility();
        Timber.d("Started count: %d", initialSize);
        subscriptions.add(model.getItemsLoadedObservable()
                .subscribe(onInitialLoading(initialSize)));
    }

    public void onStop() {
        subscriptions.clear();
    }

    public void onIngredientReceived(long ingredientTypeId) {
        Timber.d("On received ingredient type");
        subscriptions.add(model.addIngredientOfType(ingredientTypeId)
                .subscribe(notifyInserted()));
    }

    @Override
    public IngredientItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        Preconditions.checkNotNull(view);
        return new IngredientItemViewHolder(view, this);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.add_meal_ingredient_item;
    }

    @Override
    public void onBindViewHolder(IngredientItemViewHolder holder, int position) {
        holder.setPosition(position);
        final Ingredient ingredient = model.getItemAt(position);
        IngredientTemplate type = ingredient.getIngredientType();
        holder.setName(type.getName());
        holder.setEnergyDensity(namesModel.getReadableEnergyDensity(type.getEnergyDensity()));
        holder.setAmount(namesModel.getReadableAmount(ingredient.getAmount(),
                type.getEnergyDensity().getUnit()));
        holder.setCalorieCount(namesModel.getCalorieCount(ingredient.getAmount(), type.getEnergyDensity()));
    }

    @Override
    public void onIngredientClicked(@NonNull View sharedIngredientCompact, int position) {
        view.showIngredientDetails(sharedIngredientCompact, model.getItemAt(position));
    }

    @Override
    public int getItemCount() {
        return model.getItemsCount();
    }

    @NonNull
    private MealIngredientsListModel.Loading onInitialLoading(final int initialSize) {
        return new MealIngredientsListModel.Loading() {
            @Override
            public void onCompleted() {
                if (initialSize != getItemCount()) {
                    Timber.d("Started loaded count: %d", getItemCount());
                    notifyDataSetChanged();
                    setEmptyViewVisibility();
                }
            }
        };
    }

    @NonNull
    private MealIngredientsListModel.Loading notifyInserted() {
        return new MealIngredientsListModel.Loading() {
            @Override
            public void onNext(Integer itemPosition) {
                super.onNext(itemPosition);
                Timber.d("Ingredient loaded on position: %d, total: %d", itemPosition, getItemCount());
                notifyItemInserted(itemPosition);
                setEmptyViewVisibility();
            }
        };
    }

    private void setEmptyViewVisibility() {
        view.setEmptyIngredientsVisibility(Visibility.of(getItemCount() == 0));
    }

}
