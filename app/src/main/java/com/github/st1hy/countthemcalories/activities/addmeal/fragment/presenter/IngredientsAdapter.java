package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.IngredientAction;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.viewholder.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import dagger.internal.Preconditions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientItemViewHolder> implements
        IngredientItemViewHolder.Callback {
    private final AddMealView view;
    private final MealIngredientsListModel model;
    private final PhysicalQuantitiesModel quantityModel;
    private final Picasso picasso;
    private final PermissionsHelper permissionsHelper;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public IngredientsAdapter(@NonNull AddMealView view,
                              @NonNull MealIngredientsListModel model,
                              @NonNull PhysicalQuantitiesModel quantityModel,
                              @NonNull Picasso picasso,
                              @NonNull PermissionsHelper permissionsHelper) {
        this.view = view;
        this.model = model;
        this.quantityModel = quantityModel;
        this.picasso = picasso;
        this.permissionsHelper = permissionsHelper;
    }

    public void onStart() {
        notifyDataSetChanged();
        onDataSetChanged();
        subscribe(view.getIngredientActionObservable()
                .subscribe(onIngredientAction()));
        handleExtraIngredient();
    }

    private void handleExtraIngredient() {
        IngredientTemplate ingredientType = model.removeExtraIngredientType();
        if (ingredientType != null) {
            onIngredientReceived(new Ingredient(ingredientType, BigDecimal.ZERO));
        }
    }

    public void onStop() {
        subscriptions.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.add_meal_ingredient_item;
    }

    @Override
    public int getItemCount() {
        return model.getItemsCount();
    }

    @Override
    public void onIngredientClicked(@NonNull Ingredient ingredient, @NonNull final IngredientItemViewHolder viewHolder) {
        final int position = model.indexOf(ingredient);
        final List<Pair<View, String>> sharedViews = ImmutableList.of(
                pairOf(viewHolder.getImage(), "ingredient-shared-view-image")
//                pairOf(viewHolder.getRoot(), "ingredient-shared-view")
//                pairOf(viewHolder.getName(), "ingredient-shared-view-name")
//                pairOf(viewHolder.getCalories(), "ingredient-shared-view-calories"),
//                pairOf(viewHolder.getDensity(), "ingredient-shared-view-density")
        );
        viewHolder.setEnabled(false);
        enableOnNextIngredientAction(viewHolder);
        view.showIngredientDetails(position, ingredient, sharedViews);
    }

    @Override
    public IngredientItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        Preconditions.checkNotNull(view);
        return new IngredientItemViewHolder(view, this, picasso, permissionsHelper);
    }

    @Override
    public void onBindViewHolder(IngredientItemViewHolder holder, int position) {
        final Ingredient ingredient = model.getItemAt(position);
        holder.setIngredient(ingredient);
        IngredientTemplate type = ingredient.getIngredientTypeOrNull();
        holder.setName(type.getName());
        final EnergyDensity energyDensity = quantityModel.convertToPreferred(EnergyDensity.from(type));
        holder.setEnergyDensity(quantityModel.format(energyDensity));
        final AmountUnit amountUnit = energyDensity.getAmountUnit().getBaseUnit();
        final BigDecimal amount = quantityModel.convertAmountFromDatabase(ingredient.getAmount(), amountUnit);
        BigDecimal displayedAmount = amount.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
        holder.setAmount(quantityModel.format(displayedAmount, amountUnit));
        holder.setCalorieCount(quantityModel.formatEnergyCount(amount, amountUnit, energyDensity));
        onBindImage(type, holder);
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

    private void onBindImage(@NonNull IngredientTemplate ingredient,
                     @NonNull IngredientItemViewHolder holder) {
        holder.setImagePlaceholder(ingredient.getAmountType() == AmountUnitType.VOLUME ?
                R.drawable.ic_fizzy_drink : R.drawable.ic_fork_and_knife_wide);
        holder.setImageUri(ImageHolderDelegate.from(ingredient.getImageUri()));
    }

    private void enableOnNextIngredientAction(@NonNull final IngredientItemViewHolder viewHolder) {
        subscribe(view.getIngredientActionObservable()
                .first()
                .subscribe(new Action1<IngredientAction>() {
                    @Override
                    public void call(IngredientAction ingredientAction) {
                        viewHolder.setEnabled(true);
                    }
                }));
    }

    private void subscribe(Subscription subscribe) {
        subscriptions.add(subscribe);
    }

    @NonNull
    private Subscriber<IngredientAction> onIngredientAction() {
        return new SimpleSubscriber<IngredientAction>() {
            @Override
            public void onNext(IngredientAction ingredientAction) {
                switch (ingredientAction.getType()) {
                    case NEW:
                        onIngredientReceived(ingredientAction.getIngredient());
                        break;
                    case EDIT:
                        onIngredientEditFinished(ingredientAction.getId(), ingredientAction.getIngredient());
                        break;
                    case REMOVE:
                        onIngredientRemoved(ingredientAction.getId());
                        break;
                    case CANCELED:
                        break;
                }
            }
        };
    }

    private void onIngredientRemoved(long requestId) {
        if (requestId == -1L) return;
        model.removeIngredient((int) requestId);
        notifyDataSetChanged();
        onDataSetChanged();
    }

    private void onIngredientEditFinished(long requestId, @NonNull Ingredient ingredient) {
        if (requestId == -1) {
            onIngredientAdded(ingredient);
        } else {
            onIngredientEdited(requestId, ingredient);
        }
    }

    private void onIngredientEdited(long requestId, @NonNull Ingredient ingredient) {
        int position = (int) requestId;
        model.modifyIngredient(position, ingredient);
        notifyChanged(position);
    }

    private void onIngredientAdded(@NonNull Ingredient ingredient) {
        int position = model.addIngredient(ingredient);
        notifyInserted(position);
    }

    private void onIngredientReceived(@NonNull Ingredient ingredient) {
        view.showIngredientDetails(-1L, ingredient, Collections.<Pair<View, String>>emptyList());
    }

    private void notifyInserted(int position) {
        notifyItemInserted(position);
        view.scrollTo(position);
        view.showSnackbarError(Optional.<String>absent());
        onDataSetChanged();
    }

    private void notifyChanged(int position) {
        notifyItemChanged(position);
        onDataSetChanged();
    }

    private void onDataSetChanged() {
        setEmptyIngredientsVisibility();
        setTotalCalories();
    }

    private void setEmptyIngredientsVisibility() {
        view.setEmptyIngredientsVisibility(Visibility.of(getItemCount() == 0));
    }

    private void setTotalCalories() {
        Observable.from(model.getIngredients())
                .map(quantityModel.mapToEnergy())
                .map(quantityModel.sumAll())
                .lastOrDefault(BigDecimal.ZERO)
                .map(quantityModel.energyAsString())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String totalEnergy) {
                        view.setTotalEnergy(totalEnergy);
                    }
                });
    }

    @NonNull
    private static Pair<View, String> pairOf(@NonNull View view, @NonNull String string) {
        return Pair.create(view, string);
    }

}
