package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerViewNotifier;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

@PerFragment
public class MealIngredientsPresenter implements RecyclerAdapterWrapper<IngredientViewHolder>, BasicLifecycle {


    @NonNull
    private final Meal meal;
    @NonNull
    private final PhysicalQuantitiesModel quantitiesModel;
    private RecyclerViewNotifier notifier;

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private List<Ingredient> ingredients = Collections.emptyList();

    @Inject
    public MealIngredientsPresenter(@NonNull Meal meal,
                                    @NonNull PhysicalQuantitiesModel quantitiesModel) {
        this.meal = meal;
        this.quantitiesModel = quantitiesModel;
    }

    @Override
    public void setNotifier(@NonNull RecyclerViewNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void onStart() {
        ingredients = meal.getIngredients();
        notifier.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meal_detail_ingredient_item, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        IngredientTemplate ingredientType = ingredient.getIngredientTypeOrNull();
        holder.setName(ingredientType.getName());

        EnergyDensity databaseEnergyDensity = EnergyDensity.from(ingredientType);
        EnergyDensity energyDensity = quantitiesModel.convertToPreferred(databaseEnergyDensity);
        AmountUnit amountUnit = energyDensity.getAmountUnit().getBaseUnit();
        BigDecimal amount = quantitiesModel.convertAmountFromDatabase(ingredient.getAmount(), amountUnit);
        holder.setEnergy(quantitiesModel.formatEnergyCount(amount, amountUnit, energyDensity));

        BigDecimal displayedAmount = amount.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
        holder.setAmount(quantitiesModel.format(displayedAmount, amountUnit));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
