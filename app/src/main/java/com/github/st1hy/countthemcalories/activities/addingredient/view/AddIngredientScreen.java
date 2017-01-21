package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.fragment.model.Tags;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import rx.Observable;

public interface AddIngredientScreen {

    void onIngredientTemplateCreated(@NonNull IngredientTemplate template);

    @NonNull
    @CheckResult
    Observable.Transformer<Tags, Tags> selectTags();

    void showInWebBrowser(@NonNull Uri address);

    @NonNull
    @CheckResult
    Observable<Void> addTagObservable();

}
