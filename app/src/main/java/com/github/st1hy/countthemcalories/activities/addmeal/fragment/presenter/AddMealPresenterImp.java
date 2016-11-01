package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenterImp;
import com.github.st1hy.countthemcalories.database.Meal;
import com.google.common.base.Optional;

import org.parceler.Parcels;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class AddMealPresenterImp extends WithPicturePresenterImp implements AddMealPresenter {

    private final AddMealView view;
    private final AddMealModel model;
    private final Meal meal;
    private final MealIngredientsListModel ingredientsListModel;

    @Inject
    public AddMealPresenterImp(@NonNull AddMealView view,
                               @NonNull PermissionsHelper permissionsHelper,
                               @NonNull AddMealModel model,
                               @NonNull ImageHolderDelegate imageHolderDelegate,
                               @NonNull Meal meal, MealIngredientsListModel ingredientsListModel) {
        super(view, permissionsHelper, model, imageHolderDelegate);
        this.view = view;
        this.model = model;
        this.meal = meal;
        this.ingredientsListModel = ingredientsListModel;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadImageUri(meal.getImageUri());
        view.setName(meal.getName());

        subscriptions.add(view.getNameObservable().skip(1).subscribe(setNameToModel()));
        subscriptions.add(view.getAddIngredientObservable().subscribe(onAddNewIngredient()));

        subscriptions.add(view.getSaveClickedObservable()
                .filter(whenMealValid())
                .flatMap(saveMealToDatabase())
                .subscribe(closeView()));
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(AddMealModel.SAVED_MEAL_STATE, Parcels.wrap(meal));
        outState.putParcelable(MealIngredientsListModel.SAVED_INGREDIENTS, Parcels.wrap(ingredientsListModel));
    }

    @NonNull
    private Func1<Void, Boolean> whenMealValid() {
        return new Func1<Void, Boolean>() {
            @Override
            public Boolean call(Void aVoid) {
                return validateMeal();
            }
        };
    }

    @NonNull
    private Func1<Void, Observable<Void>> saveMealToDatabase() {
        return new Func1<Void, Observable<Void>>() {
            @Override
            public Observable<Void> call(Void aVoid) {
                return model.saveToDatabase();
            }
        };
    }

    @NonNull
    private SimpleSubscriber<Void> closeView() {
        return new SimpleSubscriber<Void>() {

            @Override
            public void onNext(Void aVoid) {
                view.onMealSaved();
            }
        };
    }

    @Override
    public void onImageUriChanged(@NonNull Uri uri) {
        super.onImageUriChanged(uri);
        meal.setImageUri(uri);
    }

    private boolean validateMeal() {
        return validateName() & validateIngredients();
    }

    private boolean validateName() {
        Optional<String> nameError = model.getNameError();
        view.showNameError(nameError);
        return !nameError.isPresent();
    }

    private boolean validateIngredients() {
        Optional<String> ingredientsError = model.getIngredientsError();
        view.showSnackbarError(ingredientsError);
        return !ingredientsError.isPresent();
    }

    @NonNull
    private Action1<CharSequence> setNameToModel() {
        return new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                meal.setName(charSequence.toString());
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
