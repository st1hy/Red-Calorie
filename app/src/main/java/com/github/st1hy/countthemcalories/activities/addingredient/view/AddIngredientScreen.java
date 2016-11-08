package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import java.util.Collection;

import rx.Observable;

public interface AddIngredientScreen {

    void onIngredientTemplateCreated(IngredientTemplate template);

    /**
     * @param tagNames add to be excluded
     */
    void openSelectTagScreen(@NonNull Collection<String> tagNames);

    @NonNull
    Observable<Void> getSaveObservable();

    void showInWebBrowser(@NonNull Uri address);

    ImageView getImageView();
}
