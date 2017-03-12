package com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.inject.IngredientListComponentFactory;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.inject.IngredientListModule;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import java.math.BigDecimal;

import javax.inject.Inject;

import dagger.internal.Preconditions;

@PerFragment
public class IngredientsListAdapter extends RecyclerView.Adapter<IngredientItemViewHolder> {

    @NonNull
    private final AddMealView view;
    @NonNull
    private final MealIngredientsListModel model;
    @NonNull
    private final PhysicalQuantitiesModel quantityModel;
    @NonNull
    private final IngredientListComponentFactory factory;


    @Inject
    public IngredientsListAdapter(@NonNull AddMealView view,
                                  @NonNull MealIngredientsListModel model,
                                  @NonNull PhysicalQuantitiesModel quantityModel,
                                  @NonNull IngredientListComponentFactory factory) {
        this.view = view;
        this.model = model;
        this.quantityModel = quantityModel;
        this.factory = factory;
    }

    public int getItemViewType(int position) {
        return R.layout.add_meal_ingredient_item;
    }

    @Override
    public int getItemCount() {
        return model.getItemsCount();
    }

    @Override
    public IngredientItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        Preconditions.checkNotNull(view);
        return factory.newAddMealFragmentComponent(new IngredientListModule(view))
                .getHolder();
    }

    @Override
    public void onBindViewHolder(IngredientItemViewHolder holder, int position) {
        final Ingredient ingredient = model.getItemAt(position);
        holder.setIngredient(ingredient);
        IngredientTemplate type = ingredient.getIngredientTypeOrNull();
        holder.setName(type.getDisplayName());
        final EnergyDensity energyDensity = quantityModel.convertToPreferred(EnergyDensity.from(type));
        holder.setEnergyDensity(quantityModel.format(energyDensity));
        final AmountUnit amountUnit = energyDensity.getAmountUnit().getBaseUnit();
        final BigDecimal amount = quantityModel.convertAmountFromDatabase(ingredient.getAmount(), amountUnit);
        BigDecimal displayedAmount = amount.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
        holder.setAmount(quantityModel.format(displayedAmount, amountUnit));
        holder.setCalorieCount(quantityModel.formatEnergyCount(amount, amountUnit, energyDensity));
        holder.setCalorieUnit(quantityModel.getUnitName(energyDensity.getEnergyUnit()));
        holder.setImagePlaceholder(type.getAmountType() == AmountUnitType.VOLUME ?
                R.drawable.ic_fizzy_drink : R.drawable.ic_fork_and_knife_wide);
        holder.setImageUri(type.getImageUri());
    }


    @Override
    public void onViewAttachedToWindow(IngredientItemViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onAttached();
    }

    @Override
    public void onViewDetachedFromWindow(IngredientItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onDetached();
    }
}
