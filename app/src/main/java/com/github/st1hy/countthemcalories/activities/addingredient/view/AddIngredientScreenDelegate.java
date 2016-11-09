package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Tag;

import java.util.Collection;

import rx.Observable;

public abstract class AddIngredientScreenDelegate implements AddIngredientScreen {

    protected abstract AddIngredientScreen getDelegate();

    @Override
    public void onIngredientTemplateCreated(IngredientTemplate template) {
        getDelegate().onIngredientTemplateCreated(template);
    }

    @Override
    @NonNull
    public Observable<Tag> selectTag(@NonNull Collection<String> excludedTagNames) {
        return getDelegate().selectTag(excludedTagNames);
    }

    @Override
    @NonNull
    public Observable<Void> getSaveObservable() {
        return getDelegate().getSaveObservable();
    }

    @Override
    public void showInWebBrowser(@NonNull Uri address) {
        getDelegate().showInWebBrowser(address);
    }

}
