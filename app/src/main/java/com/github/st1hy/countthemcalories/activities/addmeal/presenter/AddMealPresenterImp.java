package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenterImp;

import javax.inject.Inject;

import rx.functions.Action1;

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
        Uri imageUri = model.getImageUri();
        if (imageUri != Uri.EMPTY) onImageReceived(imageUri);
        view.setName(model.getName());

        subscriptions.add(view.getNameObservable().subscribe(setNameToModel()));
        subscriptions.add(view.getAddIngredientObservable().subscribe(onAddNewIngredient()));
        adapter.onStart();
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

    void onSaveButtonClicked() {
        view.openOverviewActivity();
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
    private Action1<Void> onAddNewIngredient() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.openAddIngredient();
            }
        };
    }


}
