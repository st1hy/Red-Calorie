package com.github.st1hy.countthemcalories.activities.mealdetail.presenter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.model.MealDetailModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MealIngredientsAdapter extends RecyclerView.Adapter<IngredientViewHolder> {

    final MealDetailModel model;
    final PhysicalQuantitiesModel quantitiesModel;

    final CompositeSubscription subscriptions = new CompositeSubscription();

    List<Ingredient> ingredients = Collections.emptyList();

    public MealIngredientsAdapter(@NonNull MealDetailModel model,
                                  @NonNull PhysicalQuantitiesModel quantitiesModel) {
        this.model = model;
        this.quantitiesModel = quantitiesModel;
    }

    public void onStart() {
        subscriptions.add(model.getMealObservable()
                .subscribe(onMealLoaded()));
    }

    public void onStop() {
        subscriptions.clear();
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.overview_extended_ingredient_item, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        IngredientTemplate ingredientType = ingredient.getIngredientType();
        holder.setName(ingredientType.getName());

        EnergyDensity databaseEnergyDensity = EnergyDensity.from(ingredientType);
        EnergyDensity energyDensity = quantitiesModel.convertToPreferred(databaseEnergyDensity);
        AmountUnit amountUnit = energyDensity.getAmountUnit().getBaseUnit();
        BigDecimal databaseAmount = ingredient.getAmount();
        holder.setEnergy(quantitiesModel.formatEnergyCount(databaseAmount, amountUnit, energyDensity));

        final BigDecimal amount = quantitiesModel.convertAmountFromDatabase(databaseAmount, amountUnit);
        holder.setAmount(quantitiesModel.format(amount, amountUnit));
    }

    @NonNull
    private Action1<Meal> onMealLoaded() {
        return new Action1<Meal>() {
            @Override
            public void call(Meal meal) {
                ingredients = meal.getIngredients();
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
