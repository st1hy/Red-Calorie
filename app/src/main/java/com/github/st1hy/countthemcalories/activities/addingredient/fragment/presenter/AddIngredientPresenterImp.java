package com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModelHelper;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTypeCreateException;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTypeCreateException.ErrorType;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.InputType;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientView;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientMenuAction;
import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.core.rx.Filters;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@PerFragment
public class AddIngredientPresenterImp implements AddIngredientPresenter {

    @NonNull
    private final AddIngredientView view;
    @NonNull
    private final AddIngredientModel model;
    @NonNull
    private final AddIngredientModelHelper modelHelper;
    @NonNull
    private final SelectPicturePresenter picturePresenter;
    @NonNull
    private final DialogView dialogView;
    @NonNull
    private final Observable<AddIngredientMenuAction> menuActionObservable;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public AddIngredientPresenterImp(@NonNull AddIngredientView view,
                                     @NonNull AddIngredientModel model,
                                     @NonNull AddIngredientModelHelper modelHelper,
                                     @NonNull DialogView dialogView,
                                     @NonNull SelectPicturePresenter picturePresenter,
                                     @NonNull Observable<AddIngredientMenuAction> menuActionObservable) {
        this.view = view;
        this.model = model;
        this.modelHelper = modelHelper;
        this.picturePresenter = picturePresenter;
        this.dialogView = dialogView;
        this.menuActionObservable = menuActionObservable;
    }

    @Override
    public void onStart() {
        picturePresenter.loadImageUri(model.getImageUri());
        view.setName(model.getName());
        view.setEnergyDensityValue(model.getEnergyValue());
        view.setSelectedUnitName(modelHelper.getEnergyDensityUnitName());

        final Observable<CharSequence> nameObservable = view.getNameObservable();
        subscribe(
                nameObservable.subscribe(c -> model.setName(c.toString()))
        );
        final Observable<CharSequence> valueObservable = view.getValueObservable();
        subscribe(
                valueObservable.subscribe(c -> model.setEnergyValue(c.toString()))
        );

        subscribe(
                Observable.combineLatest(nameObservable, valueObservable,
                        (Func2<CharSequence, CharSequence, Void>) (name, energyValue) -> {
                            onCreateIngredientResult(modelHelper.canCreateIngredient(
                                    name.toString(), energyValue.toString())
                            );
                            return null;
                        })
                        .subscribe()
        );

        subscribe(
                menuActionObservable.filter(Filters.equalTo(AddIngredientMenuAction.SAVE))
                        .map(Functions.INTO_VOID)
                        .flatMap(aVoid -> modelHelper.saveIntoDatabase())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry((attempt, error) -> {
                            if (error instanceof IngredientTypeCreateException) {
                                List<ErrorType> errors = ((IngredientTypeCreateException) error).getErrors();
                                onCreateIngredientResult(errors);
                                return true;
                            } else {
                                Timber.e(error, "Error adding new ingredient type to database");
                                return attempt < 128;
                            }
                        })
                        .subscribe(template -> {
                            onCreateIngredientResult(Collections.emptyList());
                            view.onIngredientTemplateCreated(template);
                        })
        );
        subscribe(
                view.getSearchObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aVoid -> {
                            String name = model.getName();
                            if (!name.trim().isEmpty())
                                view.showInWebBrowser(modelHelper.getSearchIngredientQuery(name));
                        })
        );
        subscribe(
                view.getSelectTypeObservable()
                        .flatMap(aVoid -> {
                            final List<Pair<AmountUnit, CharSequence>> optionsList = modelHelper.getSelectTypeDialogOptions();
                            CharSequence[] options = Collections2.transform(optionsList, input -> input.second)
                                    .toArray(new CharSequence[optionsList.size()]);
                            return dialogView.showAlertDialog(model.getSelectTypeDialogTitle(), options)
                                    .map(position -> optionsList.get(position).first);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SimpleSubscriber<AmountUnit>() {
                            @Override
                            public void onNext(AmountUnit amountUnit) {
                                model.setAmountType(amountUnit.getType());
                                view.setSelectedUnitName(modelHelper.getEnergyDensityUnitName());
                            }
                        })
        );
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    private void onCreateIngredientResult(@NonNull List<ErrorType> errors) {
        showErrorMessage(errors);
        if (!errors.isEmpty()) {
            requestFocusToField(errors.get(0));
        }
    }

    private void showErrorMessage(@NonNull List<ErrorType> errors) {
        for (ErrorType errorType : ErrorType.values()) {
            showErrorMessageIfExist(errors, errorType);
        }
    }

    private void showErrorMessageIfExist(@NonNull List<ErrorType> errors,
                                         @NonNull ErrorType lookFor) {
        Optional<Integer> errorMessage = searchListFor(errors, lookFor);
        InputType inputType = lookFor.getInputType();
        if (errorMessage.isPresent()) {
            view.showError(inputType, errorMessage.get());
        } else {
            view.hideError(inputType);
        }
    }

    private void requestFocusToField(@NonNull ErrorType firstError) {
        view.requestFocusTo(firstError.getInputType());
    }

    @NonNull
    private static Optional<Integer> searchListFor(@NonNull List<ErrorType> errors,
                                                   @NonNull ErrorType error) {
        return errors.contains(error) ? Optional.of(error.getErrorResId()) : Optional.<Integer>absent();
    }

    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }
}
