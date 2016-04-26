package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.Manifest;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealView;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class AddMealPresenterImp implements AddMealPresenter {
    private final AddMealView view;
    private final PermissionsHelper permissionsHelper;
    private final IngredientListPresenter ingredientListPresenter;

    @Inject
    public AddMealPresenterImp(@NonNull AddMealView view,
                               @NonNull PermissionsHelper permissionsHelper,
                               @NonNull IngredientListPresenter ingredientListPresenter) {
        this.view = view;
        this.permissionsHelper = permissionsHelper;
        this.ingredientListPresenter = ingredientListPresenter;
    }

    public void onSaveButtonClicked() {
        view.openOverviewActivity();
    }

    @Override
    public void onImageClicked() {
        permissionsHelper.checkPermissionAndAskIfNecessary(Manifest.permission.READ_EXTERNAL_STORAGE, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission == Permission.GRANTED) {
                            view.showSelectImageInputDialog();
                        }
                    }
                });
    }

    @Override
    public void onSelectedImageSource(@NonNull ImageSource imageSource) {
        switch (imageSource) {
            case CAMERA:
                view.openCameraAndGetPicture();
                break;
            case GALLERY:
                view.pickImageFromGallery();
                break;
        }
    }

    @Override
    public void onImageReceived(@NonNull final Uri uri) {
        view.setImageToView(uri);
    }

    @Override
    public void onImageLoadingSuccess() {
    }

    @Override
    public void onImageLoadingFailed() {

    }

    @NonNull
    @Override
    public RecyclerView.Adapter getIngredientListAdapter() {
        return ingredientListPresenter;
    }

    @Override
    public void onAddNewIngredientClicked() {
        view.openAddIngredient();
    }

    @Override
    public void onIngredientReceived(int ingredientTypeId) {
        //TODO
    }

    @Override
    public boolean onClickedOnAction(@IdRes int menuActionId) {
        if (menuActionId == R.id.action_save) {
            onSaveButtonClicked();
            return true;
        }
        return false;
    }

}
