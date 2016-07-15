package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.model.IngredientDetailModel;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.model.IngredientDetailModel.IngredientLoader;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailView;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class IngredientDetailPresenterImpl implements IngredientDetailPresenter {
    private final IngredientDetailModel model;
    private final PhysicalQuantitiesModel quantityModel;
    private final IngredientDetailView view;
    private final Picasso picasso;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private EnergyDensity energyDensity;

    @Inject
    public IngredientDetailPresenterImpl(@NonNull IngredientDetailModel model,
                                         @NonNull PhysicalQuantitiesModel quantityModel,
                                         @NonNull IngredientDetailView view,
                                         @NonNull Picasso picasso) {
        this.model = model;
        this.quantityModel = quantityModel;
        this.view = view;
        this.picasso = picasso;
    }

    @Override
    public void onStart() {
        subscriptions.add(model.getIngredientObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new IngredientLoader() {
                    @Override
                    public void onNext(Ingredient ingredient) {
                        bindViews(ingredient);
                    }
                }));
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        model.onSaveState(outState);
    }

    private void bindViews(Ingredient ingredient) {
        IngredientTemplate type = ingredient.getIngredientType();
        view.setName(type.getName());
        energyDensity = quantityModel.convertToPreferred(EnergyDensity.from(type));
        view.setEnergyDensity(quantityModel.format(energyDensity));
        final AmountUnit amountUnit = energyDensity.getAmountUnit().getBaseUnit();
        final BigDecimal amount = quantityModel.convertAmountFromDatabase(ingredient.getAmount(), amountUnit);
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            view.setAmount(amount.toPlainString());
        }
        view.setCalorieCount(quantityModel.formatEnergyCount(amount, amountUnit, energyDensity));
        bindImage(type);
        view.setUnitName(quantityModel.getUnitName(amountUnit));

        subscriptions.add(view.getAmountObservable()
                .skip(1)
                .subscribe(onAmountChanged()));
        subscriptions.add(view.getRemoveObservable().subscribe(onRemoveClicked()));
        subscriptions.add(view.getAcceptObservable()
                .filter(formCompleted())
                .subscribe(onAcceptClicked()));
    }

    private void bindImage(@NonNull IngredientTemplate type) {
        if (!Uri.EMPTY.equals(type.getImageUri())) {
            subscriptions.add(RxPicasso.Builder.with(picasso, type.getImageUri())
                    .centerCrop()
                    .fit()
                    .into(view.getImageView())
                    .asObservable()
                    .subscribe());

        } else {
            @DrawableRes int imageRes = type.getAmountType() == AmountUnitType.VOLUME ?
                    R.drawable.ic_fizzy_drink : R.drawable.ic_fork_and_knife_wide;
            view.getImageView().setImageResource(imageRes);
        }
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
        final AmountUnit amountUnit = energyDensity.getAmountUnit().getBaseUnit();
        model.setIngredientAmount(quantityModel.convertAmountToDatabase(amount, amountUnit));
        view.setCalorieCount(quantityModel.formatEnergyCount(amount, amountUnit, energyDensity));
    }

    @NonNull
    private Action1<Void> onRemoveClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.hideSoftKeyboard();
                view.removeIngredient(model.getIngredient().getId());
            }
        };
    }

    private Action1<? super Void> onAcceptClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.hideSoftKeyboard();
                Ingredient ingredient = model.getIngredient();
                view.commitEditedIngredientChanges(ingredient.getId(),
                        new IngredientTypeParcel(ingredient.getIngredientType()),
                        ingredient.getAmount());
            }
        };
    }


    @NonNull
    private Func1<? super Void, Boolean> formCompleted() {
        return new Func1<Void, Boolean>() {
            @Override
            public Boolean call(Void aVoid) {
                return checkAmountCorrect(view.getCurrentAmount());
            }
        };
    }
}
