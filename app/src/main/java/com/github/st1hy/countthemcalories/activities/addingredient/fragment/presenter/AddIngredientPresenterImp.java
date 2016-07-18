package com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel.IngredientTypeCreateException;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel.IngredientTypeCreateException.ErrorType;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientView;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenterImp;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel.IngredientTypeCreateException.ErrorType.NO_NAME;
import static com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel.IngredientTypeCreateException.ErrorType.NO_VALUE;
import static com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel.IngredientTypeCreateException.ErrorType.ZERO_VALUE;

public class AddIngredientPresenterImp extends WithPicturePresenterImp implements AddIngredientPresenter {
    private final AddIngredientView view;
    private final AddIngredientModel model;

    @Inject
    public AddIngredientPresenterImp(@NonNull AddIngredientView view,
                                     @NonNull PermissionsHelper permissionsHelper,
                                     @NonNull AddIngredientModel model,
                                     @NonNull Picasso picasso) {
        super(view, permissionsHelper, model, picasso);
        this.view = view;
        this.model = model;
    }

    @Override
    public void onStart() {
        subscriptions.add(model.getLoading()
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        onIngredientModelReady();
                    }
                }));
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        model.onSaveState(outState);
    }

    @Override
    protected void onImageReceived(@NonNull Uri uri) {
        super.onImageReceived(uri);
        model.setImageUri(uri);
    }

    private void onIngredientModelReady() {
        super.onStart();
        Uri imageUri = model.getImageUri();
        if (!imageUri.equals(Uri.EMPTY))
            subscriptions.add(loadPictureObservable(imageUri)
                    .subscribe(new SimpleSubscriber<RxPicasso.PicassoEvent>()));
        view.setName(model.getName());
        view.setEnergyDensityValue(model.getEnergyValue());
        view.setSelectedUnitName(model.getEnergyDensityUnit());

        final Observable<CharSequence> nameObservable = view.getNameObservable();
        subscriptions.add(nameObservable.subscribe(setNameToModel()));
        final Observable<CharSequence> valueObservable = view.getValueObservable();
        subscriptions.add(valueObservable.subscribe(setValueToModel()));

        subscriptions.add(Observable.combineLatest(nameObservable, valueObservable,
                combineCheckCanCreateIngredient())
                .subscribe());

        subscriptions.add(view.getSaveObservable()
                .flatMap(saveToDatabase())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(onSaveError())
                .subscribe(onAddedIngredientToDatabase()));
        subscriptions.add(view.getSearchObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSearchForIngredientClicked()));
    }

    @NonNull
    private Subscriber<? super Void> onSearchForIngredientClicked() {
        return new SimpleSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                String name = model.getName();
                if (!name.trim().isEmpty())
                    view.showInWebBrowser(model.getSearchIngredientQuery(name));
            }
        };
    }

    @NonNull
    private Func1<Void, Observable<IngredientTemplate>> saveToDatabase() {
        return new Func1<Void, Observable<IngredientTemplate>>() {
            @Override
            public Observable<IngredientTemplate> call(Void aVoid) {
                return model.saveIntoDatabase();
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
                Intent intent = new Intent();
                intent.putExtra(AddIngredientActivity.RESULT_INGREDIENT_ID_LONG, template.getId());
                view.setResultAndFinish(intent);
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
                onCreateIngredientResult(model.canCreateIngredient(name.toString(), energyValue.toString()));
                return null;
            }
        };
    }

}
