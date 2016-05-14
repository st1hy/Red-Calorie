package com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.model.UnitNamesModel;
import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.model.IngredientDetailModel;
import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.model.IngredientDetailModel.IngredientLoader;
import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view.IngredientDetailView;
import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view.IngredientDetailsActivity;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class IngredientDetailPresenterImpl implements IngredientDetailPresenter {
    private final IngredientDetailModel model;
    private final UnitNamesModel namesModel;
    private final IngredientDetailView view;
    private final Picasso picasso;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public IngredientDetailPresenterImpl(@NonNull IngredientDetailModel model,
                                         @NonNull UnitNamesModel namesModel,
                                         @NonNull IngredientDetailView view,
                                         @NonNull Picasso picasso) {
        this.model = model;
        this.namesModel = namesModel;
        this.view = view;
        this.picasso = picasso;
    }

    @Override
    public void onStart() {
        if (!model.isDataValid()) {
            view.finish();
        } else {
            subscriptions.add(model.getIngredientObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new IngredientLoader() {
                        @Override
                        public void onNext(Ingredient ingredient) {
                            super.onNext(ingredient);
                            bindViews(ingredient);
                        }
                    }));
        }
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
        view.setEnergyDensity(namesModel.getReadableEnergyDensity(type.getEnergyDensity()));
        if (ingredient.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            view.setAmount(ingredient.getAmount().toPlainString());
        }
        setCalorieCount(model.getIngredient());
        if (!Uri.EMPTY.equals(type.getImageUri())) {
            bindImage(type);
        }
        view.setUnitName(namesModel.getUnitName(type.getEnergyDensity().getUnit()));

        subscriptions.add(view.getAmountObservable()
                .skip(1)
                .subscribe(onAmountChanged()));
        subscriptions.add(view.getRemoveObservable().subscribe(onRemoveClicked()));
        subscriptions.add(view.getAcceptObservable().subscribe(onAcceptClicked()));
    }

    private void bindImage(@NonNull IngredientTemplate type) {
        subscriptions.add(RxPicasso.Builder.with(picasso, type.getImageUri())
                .centerCrop()
                .fit()
                .into(view.getImageView())
                .asObservable()
                .subscribe());
    }

    private void setCalorieCount(@NonNull Ingredient ingredient) {
        view.setCalorieCount(namesModel.getCalorieCount(ingredient.getAmount(),
                ingredient.getIngredientType().getEnergyDensity()));
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

    private void onAmountChanges(@NonNull String amount) {
        checkAmountCorrect(amount);
        model.setIngredientAmount(amount);
        setCalorieCount(model.getIngredient());
    }

    @NonNull
    private Action1<Void> onRemoveClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                finishWithResult(IngredientDetailsActivity.RESULT_REMOVE);
            }
        };
    }

    private Action1<? super Void> onAcceptClicked() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                finishWithResult(IngredientDetailsActivity.RESULT_OK);
            }
        };
    }

    private void finishWithResult(int resultCode) {
        Ingredient ingredient = model.getIngredient();
        view.setResultAndFinish(resultCode,
                ingredient.getId(),
                new IngredientTypeParcel(ingredient.getIngredientType()),
                ingredient.getAmount());
    }
}
