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
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientItemViewHolder> {
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
        subscriptions.add(model.getItemsLoadedObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MealIngredientsListModel.Loading() {
                    @Override
                    public void onCompleted() {
                        notifyDataSetChanged();
                        onDataSetChanged();
                    }
                }));
    }

    public void onStop() {
        subscriptions.clear();
    }

    public void onIngredientReceived(long ingredientTypeId) {
        subscriptions.add(model.addIngredientOfType(ingredientTypeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notifyInserted()));
    }

    @Override
    public IngredientItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        Preconditions.checkNotNull(view);
        return new IngredientItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.add_meal_ingredient_item;
    }

    @Override
    public void onBindViewHolder(IngredientItemViewHolder holder, int position) {
        final Ingredient ingredient = model.getItemAt(position);
        IngredientTemplate type = ingredient.getIngredientType();
        holder.setName(type.getName());
        holder.setEnergyDensity(namesModel.getReadableEnergyDensity(type.getEnergyDensity()));
        holder.setAmount(namesModel.getReadableAmount(ingredient.getAmount(),
                type.getEnergyDensity().getUnit()));
    }

    @Override
    public int getItemCount() {
        return model.getItemsCount();
    }

    @NonNull
    private MealIngredientsListModel.Loading notifyInserted() {
        return new MealIngredientsListModel.Loading() {
            @Override
            public void onNext(Integer itemPosition) {
                super.onNext(itemPosition);
                notifyItemInserted(itemPosition);
                onDataSetChanged();
            }
        };
    }

    private void onDataSetChanged() {
        view.setEmptyIngredientsVisibility(Visibility.of(getItemCount() == 0));
    }
}
