package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public interface AddMealPresenter {
    void onSaveButtonClicked();

    void onImageClicked();

    void onSelectedImageSource(@NonNull ImageSource imageSource);

    void onImageReceived(@NonNull Uri data);

    void onImageLoadingSuccess();

    void onImageLoadingFailed();

    @NonNull
    RecyclerView.Adapter getIngredientListAdapter();
}
