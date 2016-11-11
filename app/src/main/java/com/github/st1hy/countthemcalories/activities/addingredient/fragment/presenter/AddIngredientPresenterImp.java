package com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModelHelper;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTypeCreateException;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTypeCreateException.ErrorType;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientView;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.core.headerpicture.PicturePresenter;
import com.github.st1hy.countthemcalories.core.rx.Filters;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTypeCreateException.ErrorType.NO_NAME;
import static com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTypeCreateException.ErrorType.NO_VALUE;
import static com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTypeCreateException.ErrorType.ZERO_VALUE;

public class AddIngredientPresenterImp implements AddIngredientPresenter {

    @NonNull
    private final AddIngredientView view;
    @NonNull
    private final AddIngredientModel model;
    @NonNull
    private final AddIngredientModelHelper modelHelper;
    @NonNull
    private final PicturePresenter picturePresenter;
    @NonNull
    private final DialogView dialogView;
    @NonNull
    private final Observable<AddMealMenuAction> menuActionObservable;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public AddIngredientPresenterImp(@NonNull AddIngredientView view,
                                     @NonNull AddIngredientModel model,
                                     @NonNull AddIngredientModelHelper modelHelper,
                                     @NonNull DialogView dialogView,
                                     @NonNull PicturePresenter picturePresenter,
                                     @NonNull Observable<AddMealMenuAction> menuActionObservable) {
        this.view = view;
        this.model = model;
        this.modelHelper = modelHelper;
        this.picturePresenter = picturePresenter;
        this.dialogView = dialogView;
        this.menuActionObservable = menuActionObservable;
    }

    @Override
    public void onStart() {
        picturePresenter.onStart();
        picturePresenter.loadImageUri(model.getImageUri());
        view.setName(model.getName());
        view.setEnergyDensityValue(model.getEnergyValue());
        view.setSelectedUnitName(modelHelper.getEnergyDensityUnitName());

        final Observable<CharSequence> nameObservable = view.getNameObservable();
        subscribe(
                nameObservable.subscribe(setNameToModel())
        );
        final Observable<CharSequence> valueObservable = view.getValueObservable();
        subscribe(
                valueObservable.subscribe(setValueToModel())
        );

        subscribe(
                Observable.combineLatest(nameObservable, valueObservable,
                        combineCheckCanCreateIngredient())
                        .subscribe()
        );

        subscribe(
                menuActionObservable.filter(Filters.equalTo(AddMealMenuAction.SAVE))
                        .map(Functions.INTO_VOID)
                        .flatMap(saveToDatabase())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry(onSaveError())
                        .subscribe(onAddedIngredientToDatabase())
        );
        subscribe(
                view.getSearchObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onSearchForIngredientClicked())
        );
        subscribe(
                view.getSelectTypeObservable()
                        .flatMap(onSelectUnitClicked())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onUnitSelected())
        );
    }

    @Override
    public void onStop() {
        //NOOP
    }

    @NonNull
    private Subscriber<? super Void> onSearchForIngredientClicked() {
        return new SimpleSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                String name = model.getName();
                if (!name.trim().isEmpty())
                    view.showInWebBrowser(modelHelper.getSearchIngredientQuery(name));
            }
        };
    }

    @NonNull
    private Func1<Void, Observable<IngredientTemplate>> saveToDatabase() {
        return new Func1<Void, Observable<IngredientTemplate>>() {
            @Override
            public Observable<IngredientTemplate> call(Void aVoid) {
                return modelHelper.saveIntoDatabase();
            }
        };
    }

    @NonNull
    private Action1<CharSequence> setValueToModel() {
        return new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                model.setEnergyValue(charSequence.toString());
            }
        };
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
    private Subscriber<IngredientTemplate> onAddedIngredientToDatabase() {
        return new SimpleSubscriber<IngredientTemplate>() {
            @Override
            public void onNext(IngredientTemplate template) {
                onCreateIngredientResult(Collections.<ErrorType>emptyList());
                view.onIngredientTemplateCreated(template);
            }
        };
    }

    @NonNull
    private Func2<Integer, Throwable, Boolean> onSaveError() {
        return new Func2<Integer, Throwable, Boolean>() {
            @Override
            public Boolean call(Integer attempt, Throwable error) {
                if (error instanceof IngredientTypeCreateException) {
                    List<ErrorType> errors = ((IngredientTypeCreateException) error).getErrors();
                    onCreateIngredientResult(errors);
                    return true;
                } else {
                    Timber.e(error, "Error adding new ingredient type to database");
                    return attempt < 128;
                }
            }
        };
    }

    private void onCreateIngredientResult(@NonNull List<ErrorType> errors) {
        showErrorMessage(errors);
        if (!errors.isEmpty()) {
            requestFocusToField(errors.get(0));
        }
    }

    private void showErrorMessage(@NonNull List<ErrorType> errors) {
        view.showNameError(searchListFor(errors, NO_NAME));
        if (errors.contains(NO_VALUE)) {
            view.showValueError(Optional.of(NO_VALUE.getErrorResId()));
        } else {
            view.showValueError(searchListFor(errors, ZERO_VALUE));
        }
    }

    private void requestFocusToField(@NonNull ErrorType firstError) {
        switch (firstError) {
            case NO_NAME:
                view.requestFocusToName();
                break;
            case NO_VALUE:
            case ZERO_VALUE:
                view.requestFocusToValue();
                break;
        }
    }

    @NonNull
    private static Optional<Integer> searchListFor(@NonNull List<ErrorType> errors,
                                                   @NonNull ErrorType error) {
        return errors.contains(error) ? Optional.of(error.getErrorResId()) : Optional.<Integer>absent();
    }

    @NonNull
    private Func2<CharSequence, CharSequence, Void> combineCheckCanCreateIngredient() {
        return new Func2<CharSequence, CharSequence, Void>() {
            @Override
            public Void call(CharSequence name, CharSequence energyValue) {
                onCreateIngredientResult(modelHelper.canCreateIngredient(name.toString(), energyValue.toString()));
                return null;
            }
        };
    }

    @NonNull
    private Func1<Void, Observable<AmountUnit>> onSelectUnitClicked() {
        return new Func1<Void, Observable<AmountUnit>>() {
            @Override
            public Observable<AmountUnit> call(Void aVoid) {
                final List<Pair<AmountUnit, CharSequence>> optionsList = modelHelper.getSelectTypeDialogOptions();
                CharSequence[] options = Collections2.transform(optionsList, intoCharSequence())
                        .toArray(new CharSequence[optionsList.size()]);
                return dialogView.showAlertDialog(model.getSelectTypeDialogTitle(), options)
                        .map(intoAmountUnit(optionsList));
            }
        };
    }

    @NonNull
    private static Func1<Integer, AmountUnit> intoAmountUnit(final List<Pair<AmountUnit, CharSequence>> optionsList) {
        return new Func1<Integer, AmountUnit>() {
            @Override
            public AmountUnit call(Integer position) {
                return optionsList.get(position).first;
            }
        };
    }

    @NonNull
    private static Function<Pair<AmountUnit, CharSequence>, CharSequence> intoCharSequence() {
        return new Function<Pair<AmountUnit, CharSequence>, CharSequence>() {
            @Nullable
            @Override
            public CharSequence apply(Pair<AmountUnit, CharSequence> input) {
                return input.second;
            }
        };
    }

    @NonNull
    private SimpleSubscriber<AmountUnit> onUnitSelected() {
        return new SimpleSubscriber<AmountUnit>() {
            @Override
            public void onNext(AmountUnit amountUnit) {
                model.setAmountType(amountUnit.getType());
                view.setSelectedUnitName(modelHelper.getEnergyDensityUnitName());
            }
        };
    }

    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }
}
