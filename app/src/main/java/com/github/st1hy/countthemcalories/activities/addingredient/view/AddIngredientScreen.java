package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Tag;

import java.util.Collection;

import rx.Observable;

public interface AddIngredientScreen {

    void onIngredientTemplateCreated(IngredientTemplate template);

    @NonNull
    Observable<Tag> selectTag(@NonNull Collection<String> excludedTagNames);

    void showInWebBrowser(@NonNull Uri address);

}
