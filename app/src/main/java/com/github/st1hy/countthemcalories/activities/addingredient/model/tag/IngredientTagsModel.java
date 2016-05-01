package com.github.st1hy.countthemcalories.activities.addingredient.model.tag;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.Tag;

import javax.inject.Inject;

import rx.Observable;

public class IngredientTagsModel {

    @Inject
    public IngredientTagsModel() {
    }

    /**
     * @return observable called when loading is finished
     */
    public Observable<Void> loadTags() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public int getSize() {
        return 0;
    }

    @NonNull
    public Tag getTagAt(int position) {
        //TODO
        throw new UnsupportedOperationException();
    }
}
