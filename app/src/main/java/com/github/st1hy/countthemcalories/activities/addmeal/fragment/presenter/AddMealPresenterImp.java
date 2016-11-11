package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.core.headerpicture.PicturePresenter;
import com.github.st1hy.countthemcalories.core.rx.Filters;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.database.Meal;
import com.google.common.base.Optional;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class AddMealPresenterImp implements AddMealPresenter {

    @NonNull
    private final AddMealView view;
    @NonNull
    private final AddMealModel model;
    @NonNull
    private final Meal meal;
    @NonNull
    private final PicturePresenter picturePresenter;
    @NonNull
    private final Observable<AddMealMenuAction> menuActionObservable;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public AddMealPresenterImp(@NonNull AddMealView view,
                               @NonNull AddMealModel model,
                               @NonNull Meal meal,
                               @NonNull PicturePresenter picturePresenter,
                               @NonNull Observable<AddMealMenuAction> menuActionObservable) {
        this.view = view;
        this.model = model;
        this.meal = meal;
        this.picturePresenter = picturePresenter;
        this.menuActionObservable = menuActionObservable;
    }

    @Override
    public void onStart() {
        picturePresenter.onStart();
        picturePresenter.loadImageUri(meal.getImageUri());

        view.setName(meal.getName());
        subscribe(
                view.getNameObservable()
                        .skip(1)
                        .subscribe(setNameToModel())
        );
        subscribe(
                menuActionObservable.filter(Filters.equalTo(AddMealMenuAction.SAVE))
                        .map(Functions.INTO_VOID)
                        .filter(whenMealValid())
                        .flatMap(saveMealToDatabase())
                        .subscribe(closeView())
        );
    }

    @Override
    public void onStop() {
        picturePresenter.onStop();
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

    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }

}
