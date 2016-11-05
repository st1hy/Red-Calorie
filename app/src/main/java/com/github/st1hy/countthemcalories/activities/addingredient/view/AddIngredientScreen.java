package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.core.picture.PictureViewController;

import java.util.Collection;

import rx.Observable;

public interface AddIngredientScreen extends PictureViewController {

    void setResultAndFinish(@NonNull Intent intent);

    /**
     * @param tagNames add to be excluded
     */
    void openSelectTagScreen(@NonNull Collection<String> tagNames);

    @NonNull
    Observable<Void> getSaveObservable();

    void showInWebBrowser(@NonNull Uri address);

    ImageView getImageView();
}
