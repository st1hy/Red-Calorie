package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_TEMPLATE_PARCEL;

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
        subscriptions.add(model.getLoading()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(setupView()));
    }

    @NonNull
    private Action1<Void> setupView() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Uri imageUri = model.getImageUri();
                if (!imageUri.equals(Uri.EMPTY)) onImageReceived(imageUri);
                view.setName(model.getName());

                subscriptions.add(view.getNameObservable().skip(1).subscribe(setNameToModel()));
                subscriptions.add(view.getAddIngredientObservable().subscribe(onAddNewIngredient()));

                adapter.onStart();
            }
        };
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
    public boolean handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AddMealActivity.REQUEST_PICK_INGREDIENT) {
            if (!handlePickIngredientResult(resultCode, data)) {
                Timber.w("Pick ingredient returned incomplete result: %d, intent: %s", resultCode, data);
            }
            return true;
        } else if (requestCode == AddMealActivity.REQUEST_EDIT_INGREDIENT) {
            EditIngredientResult result = EditIngredientResult.fromIngredientDetailResult(resultCode);
            if (!handleEditIngredientResponse(result, data)) {
                Timber.w("Edit ingredient returned incomplete result: %d, intent: %s", resultCode, data);
            }
            return true;
        } else return false;
    }

    /**
     * @return false if data or resultCode was in incorrect state
     */
    private boolean handlePickIngredientResult(int resultCode, @Nullable Intent data) {
        if (resultCode == IngredientsActivity.RESULT_OK) {
            if (data == null) return false;
            IngredientTypeParcel typeParcel = data.getParcelableExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL);
            if (typeParcel == null) return false;
            adapter.onIngredientReceived(typeParcel);
        }
        return true;
    }

    /**
     * @return false if data or result was in incorrect state
     */
    private boolean handleEditIngredientResponse(@NonNull EditIngredientResult result, @Nullable Intent data) {
        if (data == null) return false;
        long requestId = data.getLongExtra(EXTRA_INGREDIENT_ID_LONG, -2L);
        if (requestId == -2L) return false;
        switch (result) {
            case REMOVE:
                adapter.onIngredientRemoved(requestId);
                break;
            case EDIT:
                IngredientTypeParcel parcel = data.getParcelableExtra(EXTRA_INGREDIENT_TEMPLATE_PARCEL);
                String stringExtra = data.getStringExtra(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL);
                if (parcel == null || stringExtra == null) return false;
                BigDecimal amount = EnergyDensityUtils.getOrZero(stringExtra);
                adapter.onIngredientEditFinished(requestId, parcel, amount);
                break;
            case UNKNOWN:
                return false;
        }
        return true;
    }

    void onSaveButtonClicked() {
        if (validateMeal()) {
            model.saveToDatabase().subscribe(new Subscriber<Void>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    Timber.e(e, "Error saving meal to database");
                }

                @Override
                public void onNext(Void aVoid) {
                    view.setResultAndFinish();
                }
            });
        }
    }

    private boolean validateMeal() {
        return validateName() & validateIngredients();
    }

    private boolean validateName() {
        String nameError = model.getNameError();
        view.showNameError(nameError);
        return nameError == null;
    }

    private boolean validateIngredients() {
        String ingredientsError = model.getIngredientsError();
        view.showIngredientsError(ingredientsError);
        return ingredientsError == null;
    }

    @NonNull
    private Action1<CharSequence> setNameToModel() {
        return new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                model.setName(charSequence.toString());
                validateName();
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
