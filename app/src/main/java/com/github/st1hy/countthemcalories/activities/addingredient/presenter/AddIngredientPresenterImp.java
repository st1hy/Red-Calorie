package com.github.st1hy.countthemcalories.activities.addingredient.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientView;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.ui.withpicture.presenter.WithPicturePresenterImp;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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
        subscriptions.add(model.getUnitObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .map(model.unitAsString())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        view.setSelectedUnitName(s);
                    }
                }));
        view.setName(model.getName());
        view.setEnergyDensityValue(model.getEnergyValue());
        Uri imageUri = model.getImageUri();
        if (imageUri != Uri.EMPTY) onImageReceived(imageUri);

        final Observable<CharSequence> nameObservable = view.getNameObservable().share();
        subscriptions.add(nameObservable.subscribe(setNameToModel()));
        final Observable<CharSequence> valueObservable = view.getValueObservable().share();
        subscriptions.add(valueObservable.subscribe(setValueToModel()));

//        Observable.combineLatest(nameObservable, valueObservable, new Func2<CharSequence, CharSequence, CharSequence[]>() {
//            @Override
//            public CharSequence[] call(CharSequence s, CharSequence s2) {
//                return new CharSequence[]{s, s2};
//            }
//        });
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        model.onSaveState(outState);
    }

    @Override
    public boolean onClickedOnAction(int itemId) {
        if (itemId == R.id.action_save) {
            onSaveClicked();
        }
        return false;
    }

    @Override
    public void onSelectUnitClicked() {
        view.showAlertDialog(model.getSelectUnitDialogTitle(), model.getUnitSelectionOptions())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer which) {
                        onUnitSelected(which);
                    }
                });
    }

    private void onUnitSelected(int which) {
        EnergyDensityUnit selectedUnit = model.getUnitSelection()[which];
        model.setUnit(selectedUnit);
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
        if (model.canCreateIngredient()) {
            model.insertIntoDatabase()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onAddedIngredientToDatabase());
        }
    }

    @NonNull
    private Action1<Void> onAddedIngredientToDatabase() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.setResultAndFinish();
            }
        };
    }
}
