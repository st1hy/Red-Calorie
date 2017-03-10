package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

@PerFragment
public class MealIngredientsAdapter extends RecyclerView.Adapter<IngredientViewHolder> {

    @NonNull
    private final PhysicalQuantitiesModel quantitiesModel;

    private final List<Ingredient> ingredients;

    @Inject
    public MealIngredientsAdapter(@NonNull List<Ingredient> ingredients,
                                  @NonNull PhysicalQuantitiesModel quantitiesModel) {
        this.quantitiesModel = quantitiesModel;
        this.ingredients = ingredients;
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
        holder.setEnergy(quantitiesModel.formatEnergyCountAndUnit(amount, amountUnit, energyDensity));

        BigDecimal displayedAmount = amount.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
        holder.setAmount(quantitiesModel.format(displayedAmount, amountUnit));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
