package com.github.st1hy.countthemcalories.ui.activities.addingredient.view;

import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.tags.model.Tags;
import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplate;

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