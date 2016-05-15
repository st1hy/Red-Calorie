package com.github.st1hy.countthemcalories.activities.addingredient.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientView;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenterImp;
import com.google.common.base.Optional;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.NO_NAME;
import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.NO_VALUE;
import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.ZERO_VALUE;

public class AddIngredientPresenterImp extends WithPicturePresenterImp implements AddIngredientPresenter {
    private final AddIngredientView view;
    private final AddIngredientModel model;

    @Inject
    public AddIngredientPresenterImp(@NonNull AddIngredientView view,
                                     @NonNull PermissionsHelper permissionsHelper,
                                     @NonNull AddIngredientModel model) {
        super(view, permissionsHelper, model);
        this.view = view;
        this.model = model;
    }

    @Override
    public void onStart() {
        Uri imageUri = model.getImageUri();
        if (imageUri != Uri.EMPTY) onImageReceived(imageUri);
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
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        model.onSaveState(outState);
    }

    @Override
    public boolean onClickedOnAction(int itemId) {
        if (itemId == R.id.action_save) {
            onSaveClicked();
            return true;
        }
        return false;
    }

    @Override
    public void onImageReceived(@NonNull Uri uri) {
        super.onImageReceived(uri);
        model.setImageUri(uri);
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

    private void onSaveClicked() {
        model.insertIntoDatabase()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onAddedIngredientToDatabase());

    }

    @NonNull
    private Subscriber<List<IngredientTypeCreateError>> onAddedIngredientToDatabase() {
        return new Subscriber<List<IngredientTypeCreateError>>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error adding new ingredient type to database");
            }

            @Override
            public void onNext(List<IngredientTypeCreateError> errors) {
                onCreateIngredientResult(errors);
                if (errors.isEmpty())
                    view.setResultAndFinish();
            }
        };
    }

    private void onCreateIngredientResult(@NonNull List<IngredientTypeCreateError> errors) {
        showErrorMessage(errors);
        if (!errors.isEmpty()) {
            requestFocusToField(errors.get(0));
        }
    }

    private void showErrorMessage(@NonNull List<IngredientTypeCreateError> errors) {
        view.showNameError(searchListFor(errors, NO_NAME));
        if (errors.contains(NO_VALUE)) {
            view.showValueError(Optional.of(NO_VALUE.getErrorResId()));
        } else {
            view.showValueError(searchListFor(errors, ZERO_VALUE));
        }
    }

    private void requestFocusToField(@NonNull IngredientTypeCreateError firstError) {
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
    private Optional<Integer> searchListFor(@NonNull List<IngredientTypeCreateError> errors,
                                            @NonNull IngredientTypeCreateError error) {
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
