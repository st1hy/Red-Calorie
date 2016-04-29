package com.github.st1hy.countthemcalories.activities.addingredient.presenter;

import android.os.Bundle;
import android.support.annotation.IdRes;
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
import timber.log.Timber;

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
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        model.onSaveState(outState);
    }

    @Override
    public void onNameTextChanges(Observable<CharSequence> observable) {
        observable.subscribe(new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                Timber.d("Name changed: %s", charSequence);
            }
        });
    }

    @Override
    public boolean onClickedOnAction(@IdRes int menuActionId) {
        if (menuActionId == R.id.action_save) {
            onSaveActionClicked();
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


    public void onSaveActionClicked() {
        view.openIngredientsScreen();
    }
}
