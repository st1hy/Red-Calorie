package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.model.SelectTagParams;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Tag;

import rx.Observable;

public interface AddIngredientScreen {

    void onIngredientTemplateCreated(@NonNull IngredientTemplate template);

    @NonNull
    @CheckResult
    Observable.Transformer<SelectTagParams, Tag> selectTag();

    void showInWebBrowser(@NonNull Uri address);

}
