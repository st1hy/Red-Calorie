package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.core.rx.Filters;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.google.common.base.Optional;

import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class AddMealPresenterImp implements AddMealPresenter {

    @NonNull
    private final AddMealView view;
    @NonNull
    private final AddMealModel model;
    @NonNull
    private final Meal meal;
    @NonNull
    private final SelectPicturePresenter picturePresenter;
    @NonNull
    private final Observable<AddMealMenuAction> menuActionObservable;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public AddMealPresenterImp(@NonNull AddMealView view,
                               @NonNull AddMealModel model,
                               @NonNull Meal meal,
                               @NonNull SelectPicturePresenter picturePresenter,
                               @NonNull Observable<AddMealMenuAction> menuActionObservable) {
        this.view = view;
        this.model = model;
        this.meal = meal;
        this.picturePresenter = picturePresenter;
        this.menuActionObservable = menuActionObservable;
    }

    @Override
    public void onStart() {
        picturePresenter.loadImageUri(meal.getImageUri());
        view.setName(meal.getName());
        subscribe(
                view.getNameObservable()
                        .skip(1)
                        .subscribe(charSequence -> {
                            meal.setName(charSequence.toString());
                        })
        );
        subscribe(
                menuActionObservable.filter(Filters.equalTo(AddMealMenuAction.SAVE))
                        .map(Functions.INTO_VOID)
                        .filter(aVoid -> validateMeal())
                        .flatMap(aVoid1 -> model.saveToDatabase())
                        .subscribe(v -> view.onMealSaved())
        );
        setupTime();
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }


    private boolean validateMeal() {
        return validateIngredients();
    }

    private boolean validateIngredients() {
        Optional<String> ingredientsError = model.getIngredientsError();
        if (ingredientsError.isPresent()) view.showSnackbarError(ingredientsError.get());
        else view.hideSnackbarError();
        return !ingredientsError.isPresent();
    }

    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }

    private void setupTime() {
        setMealTime(model.getDisplayTime());
        if (meal.getCreationDate() == null) {
            subscribe(
                    Observable.interval(model.getTimeToNextMinuteMils(), 60_000L,
                            TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                            .takeWhile(ignore -> meal.getCreationDate() == null)
                            .map(ignore -> DateTime.now())
                            .subscribe(this::setMealTime)
            );
        }
        subscribe(
                view.mealTimeClicked()
                        .compose(view.openTimePicker(model.getDisplayTime()))
                        .subscribe(selectedTime -> {
                            meal.setCreationDate(selectedTime);
                            setMealTime(selectedTime);
                        })
        );
    }

    private void setMealTime(DateTime now) {
        view.setMealTime(model.formatTime(now));
        view.setHint(model.getMealNameHint());
    }

}
