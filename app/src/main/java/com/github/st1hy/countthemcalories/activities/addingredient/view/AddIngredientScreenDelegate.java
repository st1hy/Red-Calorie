package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.model.SelectTagParams;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Tag;

import rx.Observable;

public abstract class AddIngredientScreenDelegate implements AddIngredientScreen {

    protected abstract AddIngredientScreen getDelegate();

    @Override
    public void onIngredientTemplateCreated(IngredientTemplate template) {
        getDelegate().onIngredientTemplateCreated(template);
    }

    @Override
    @NonNull
    @CheckResult
    public Observable.Transformer<SelectTagParams, Tag> selectTag() {
        return getDelegate().selectTag();
    }

    @Override
    public void showInWebBrowser(@NonNull Uri address) {
        getDelegate().showInWebBrowser(address);
    }

}
