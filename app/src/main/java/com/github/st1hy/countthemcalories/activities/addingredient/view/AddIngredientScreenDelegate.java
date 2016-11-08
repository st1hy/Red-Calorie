package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import java.util.Collection;

import rx.Observable;

public abstract class AddIngredientScreenDelegate implements AddIngredientScreen {

    protected abstract AddIngredientScreen getDelegate();

    @Override
    public void onIngredientTemplateCreated(IngredientTemplate template) {
        getDelegate().onIngredientTemplateCreated(template);
    }

    @Override
    public void openSelectTagScreen(@NonNull Collection<String> tagNames) {
        getDelegate().openSelectTagScreen(tagNames);
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

    @Override
    public ImageView getImageView() {
        return getDelegate().getImageView();
    }

}
