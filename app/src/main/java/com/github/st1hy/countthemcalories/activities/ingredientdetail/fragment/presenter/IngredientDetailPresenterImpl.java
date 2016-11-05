package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.model.IngredientDetailModel;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailView;
import com.github.st1hy.countthemcalories.core.picture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;

import org.parceler.Parcels;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.picture.imageholder.ImageHolderDelegate.from;

public class IngredientDetailPresenterImpl implements IngredientDetailPresenter {
    private final IngredientDetailModel model;
    private final IngredientDetailView view;
    private final ImageHolderDelegate imageHolderDelegate;
    private final Ingredient ingredient;
    private final PhysicalQuantitiesModel quantityModel;
    private final long ingredientID;

    private final CompositeSubscription subscriptions = new CompositeSubscription();


    @Inject
    public IngredientDetailPresenterImpl(@NonNull IngredientDetailModel model,
                                         @NonNull PhysicalQuantitiesModel quantityModel,
                                         @NonNull IngredientDetailView view,
                                         @NonNull ImageHolderDelegate imageHolderDelegate,
                                         @NonNull Ingredient ingredient,
                                         long ingredientID) {
        this.model = model;
        this.ingredient = ingredient;
        this.quantityModel = quantityModel;
        this.view = view;
        this.imageHolderDelegate = imageHolderDelegate;
        this.ingredientID = ingredientID;
    }

    @Override
    public void onStart() {
        imageHolderDelegate.onAttached();
        IngredientTemplate ingredientTemplate = ingredient.getIngredientTypeOrNull();
        view.setName(ingredientTemplate.getName());
        final EnergyDensity energyDensity = getEnergyDensity();
        view.setEnergyDensity(quantityModel.format(energyDensity));
        final AmountUnit amountUnit = energyDensity.getAmountUnit().getBaseUnit();
        final BigDecimal amount = quantityModel.convertAmountFromDatabase(ingredient.getAmount(), amountUnit);
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            view.setAmount(amount.toPlainString());
        }
        view.setCalorieCount(quantityModel.formatEnergyCount(amount, amountUnit, energyDensity));
        view.setUnitName(quantityModel.getUnitName(amountUnit));

        subscriptions.add(view.getAmountObservable()
                .skip(1)
                .subscribe(onAmountChanged()));
        subscriptions.add(view.getRemoveObservable().subscribe(onRemoveClicked()));
        subscriptions.add(view.getAcceptObservable()
                .filter(fromCompleted())
                .subscribe(onAcceptClicked()));

        imageHolderDelegate.setImagePlaceholder(ingredientTemplate.getAmountType() == AmountUnitType.VOLUME ?
                R.drawable.ic_fizzy_drink : R.drawable.ic_fork_and_knife_wide);
        imageHolderDelegate.displayImage(from(ingredientTemplate.getImageUri()));
    }

    @Override
    public void onStop() {
        subscriptions.clear();
        imageHolderDelegate.onDetached();
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(IngredientDetailModel.SAVED_INGREDIENT_MODEL, Parcels.wrap(ingredient));
    }

    private boolean checkAmountCorrect(@NonNull String amount) {
        String errorString = model.getErrorString(amount);
        view.setAmountError(errorString);
        return errorString == null;
    }

    @NonNull
    private Action1<CharSequence> onAmountChanged() {
        return new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                onAmountChanges(charSequence.toString());
            }
        };
    }

    private void onAmountChanges(@NonNull String amountString) {
        checkAmountCorrect(amountString);
        final BigDecimal amount = EnergyDensityUtils.getOrZero(amountString);
        final EnergyDensity energyDensity = getEnergyDensity();
        final AmountUnit amountUnit = energyDensity.getAmountUnit().getBaseUnit();
        ingredient.setAmount(quantityModel.convertAmountToDatabase(amount, amountUnit));
        view.setCalorieCount(quantityModel.formatEnergyCount(amount, amountUnit, energyDensity));
    }


    @NonNull
    private EnergyDensity getEnergyDensity() {
        return quantityModel.convertToPreferred(EnergyDensity.from(ingredient.getIngredientTypeOrNull()));
    }

    @NonNull
    private Action1<Void> onRemoveClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.hideSoftKeyboard();
                view.removeIngredient(ingredientID);
            }
        };
    }

    private Action1<? super Void> onAcceptClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.hideSoftKeyboard();
                view.commitEditedIngredientChanges(ingredientID, ingredient);
            }
        };
    }

    @NonNull
    private Func1<? super Void, Boolean> fromCompleted() {
        return new Func1<Void, Boolean>() {
            @Override
            public Boolean call(Void aVoid) {
                return checkAmountCorrect(view.getCurrentAmount());
            }
        };
    }
}
