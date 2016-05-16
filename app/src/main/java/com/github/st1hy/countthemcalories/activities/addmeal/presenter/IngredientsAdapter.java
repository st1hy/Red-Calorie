package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.EditIngredientResult;
import com.github.st1hy.countthemcalories.activities.addmeal.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.viewholder.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

import dagger.internal.Preconditions;
import rx.subscriptions.CompositeSubscription;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientItemViewHolder> implements
        IngredientItemViewHolder.Callback {
    private final AddMealView view;
    private final MealIngredientsListModel model;
    private final PhysicalQuantitiesModel quantityModel;
    private final Picasso picasso;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public IngredientsAdapter(@NonNull AddMealView view, @NonNull MealIngredientsListModel model,
                              @NonNull PhysicalQuantitiesModel quantityModel, @NonNull Picasso picasso) {
        this.view = view;
        this.model = model;
        this.quantityModel = quantityModel;
        this.picasso = picasso;
    }

    public void onStart() {
        final int initialSize = getItemCount();
        setEmptyViewVisibility();
        subscriptions.add(model.getItemsLoadedObservable()
                .subscribe(onInitialLoading(initialSize)));
    }

    public void onStop() {
        subscriptions.clear();
    }

    public void onIngredientReceived(@NonNull IngredientTypeParcel typeParcel) {
        view.showIngredientDetails(-1L, typeParcel, BigDecimal.ZERO, null, null);
    }

    public void onIngredientEditFinished(long requestId, @NonNull IngredientTypeParcel typeParcel,
                                         @NonNull BigDecimal amount, @NonNull EditIngredientResult result) {
        int position = (int) requestId;
        switch (result) {
            case EDIT:
                if (requestId == -1) {
                    onIngredientAdded(typeParcel, amount);
                } else {
                    onIngredientEdited(position, typeParcel, amount);
                }
                break;
            case REMOVE:
                if (requestId != -1) {
                    onIngredientRemoved(position);
                }
        }
    }

    private void onIngredientAdded(@NonNull IngredientTypeParcel typeParcel,
                                   @NonNull BigDecimal amount) {
        subscriptions.add(model.addIngredientOfType(typeParcel, amount)
                .subscribe(notifyInserted()));
    }

    private void onIngredientEdited(int position, @NonNull IngredientTypeParcel typeParcel,
                                    @NonNull BigDecimal amount) {
        subscriptions.add(model.modifyIngredient(position, typeParcel, amount)
                .subscribe(notifyChanged()));
    }

    private void onIngredientRemoved(int position) {
        model.removeIngredient(position);
        notifyDataSetChanged();
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
    public void onIngredientClicked(@NonNull View sharedElement,
                                    @NonNull Ingredient ingredient,
                                    @NonNull String sharedElementName) {
        IngredientTypeParcel typeParcel = new IngredientTypeParcel(ingredient.getIngredientType());
        BigDecimal amount = ingredient.getAmount();
        int position = model.indexOf(ingredient);
        view.showIngredientDetails(position, typeParcel, amount, sharedElement, sharedElementName);
    }

    @Override
    public IngredientItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        Preconditions.checkNotNull(view);
        return new IngredientItemViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(IngredientItemViewHolder holder, int position) {
        final Ingredient ingredient = model.getItemAt(position);
        holder.setIngredient(ingredient);
        IngredientTemplate type = ingredient.getIngredientType();
        holder.setName(type.getName());
        final EnergyDensity energyDensity = quantityModel.convertToPreferred(EnergyDensity.from(type));
        holder.setEnergyDensity(quantityModel.format(energyDensity));
        final AmountUnit amountUnit = energyDensity.getAmountUnit().getBaseUnit();
        final BigDecimal amount = quantityModel.convertAmountFromDatabase(ingredient.getAmount(), amountUnit);
        holder.setAmount(quantityModel.format(amount, amountUnit));
        holder.setCalorieCount(quantityModel.formatEnergyCount(amount, amountUnit, energyDensity));
        onBindImage(type, holder);
    }

    void onBindImage(@NonNull IngredientTemplate ingredient,
                     @NonNull IngredientItemViewHolder holder) {
        picasso.cancelRequest(holder.getImage());
        Uri imageUri = ingredient.getImageUri();
        if (imageUri != null && !imageUri.equals(Uri.EMPTY)) {
            RxPicasso.Builder.with(picasso, imageUri)
                    .centerCrop()
                    .fit()
                    .into(holder.getImage());
        } else {
            @DrawableRes int imageRes = ingredient.getAmountType() == AmountUnitType.VOLUME ?
                    R.drawable.ic_fizzy_drink : R.drawable.ic_fork_and_knife_wide;
            holder.getImage().setImageResource(imageRes);
        }
    }

    @NonNull
    private MealIngredientsListModel.Loading onInitialLoading(final int initialSize) {
        return new MealIngredientsListModel.Loading() {
            @Override
            public void onCompleted() {
                if (initialSize != getItemCount()) {
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
                notifyItemInserted(itemPosition);
                view.scrollTo(itemPosition);
                setEmptyViewVisibility();
            }
        };
    }

    @NonNull
    private MealIngredientsListModel.Loading notifyChanged() {
        return new MealIngredientsListModel.Loading() {
            @Override
            public void onNext(Integer itemPosition) {
                super.onNext(itemPosition);
                notifyItemChanged(itemPosition);
                setEmptyViewVisibility();
            }
        };
    }

    private void setEmptyViewVisibility() {
        view.setEmptyIngredientsVisibility(Visibility.of(getItemCount() == 0));
    }

}
