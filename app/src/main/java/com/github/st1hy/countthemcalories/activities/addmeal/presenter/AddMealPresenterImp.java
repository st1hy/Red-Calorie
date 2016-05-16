package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.model.EditIngredientResult;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenterImp;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.functions.Action1;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity.EXTRA_INGREDIENT_ID_LONG;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity.EXTRA_INGREDIENT_TEMPLATE_PARCEL;

public class AddMealPresenterImp extends WithPicturePresenterImp implements AddMealPresenter {
    private final AddMealView view;
    private final AddMealModel model;
    private final IngredientsAdapter adapter;

    @Inject
    public AddMealPresenterImp(@NonNull AddMealView view,
                               @NonNull PermissionsHelper permissionsHelper,
                               @NonNull IngredientsAdapter adapter,
                               @NonNull AddMealModel model) {
        super(view, permissionsHelper, model);
        this.view = view;
        this.adapter = adapter;
        this.model = model;
    }

    @Override
    public void onStart() {
        Uri imageUri = model.getImageUri();
        if (imageUri != Uri.EMPTY) onImageReceived(imageUri);
        view.setName(model.getName());

        subscriptions.add(view.getNameObservable().subscribe(setNameToModel()));
        subscriptions.add(view.getAddIngredientObservable().subscribe(onAddNewIngredient()));
        adapter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.onStop();
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        model.onSaveState(outState);
    }

    @Override
    public boolean onClickedOnAction(@IdRes int menuActionId) {
        if (menuActionId == R.id.action_save) {
            onSaveButtonClicked();
            return true;
        }
        return false;
    }

    @Override
    public void onImageReceived(@NonNull Uri uri) {
        super.onImageReceived(uri);
        model.setImageUri(uri);
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AddMealActivity.REQUEST_PICK_INGREDIENT) {
            if (resultCode == IngredientsActivity.RESULT_OK) {
                IngredientTypeParcel typeParcel = data.getParcelableExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL);
                if (typeParcel != null) {
                    adapter.onIngredientReceived(typeParcel);
                } else {
                    Timber.w("Pick ingredient returned incomplete result ok");
                }
            }
            return true;
        } else if (requestCode == AddMealActivity.REQUEST_EDIT_INGREDIENT) {
            EditIngredientResult result = EditIngredientResult.fromIngredientDetailResult(resultCode);
            if (result != EditIngredientResult.UNKNOWN ) {
                long requestId = data.getLongExtra(EXTRA_INGREDIENT_ID_LONG, -2L);
                IngredientTypeParcel parcel = data.getParcelableExtra(EXTRA_INGREDIENT_TEMPLATE_PARCEL);
                String stringExtra = data.getStringExtra(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL);
                if (requestId != -2L && parcel != null && stringExtra != null) {
                    BigDecimal amount = EnergyDensityUtils.getOrZero(stringExtra);
                    adapter.onIngredientEditFinished(requestId, parcel, amount, result);
                } else {
                    Timber.w("Edit ingredient returned incomplete result: %s, intent: %s", result, data);
                }
            }
            return true;
        } else return false;
    }

    void onSaveButtonClicked() {
        view.openOverviewActivity();
    }

    @NonNull
    private Action1<CharSequence> setNameToModel() {
        return new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                model.setName(charSequence.toString());
            }
        };
    }

    @NonNull
    private Action1<Void> onAddNewIngredient() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.openAddIngredient();
            }
        };
    }


}
