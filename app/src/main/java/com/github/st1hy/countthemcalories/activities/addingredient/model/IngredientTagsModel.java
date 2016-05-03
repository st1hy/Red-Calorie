package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.model.TagsModel;
import com.github.st1hy.countthemcalories.database.Tag;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

public class IngredientTagsModel {
    final TagsModel tagsModel;

    private List<Tag> tags = new ArrayList<>(4);

    public IngredientTagsModel(TagsModel tagsModel) {
        this.tagsModel = tagsModel;
    }

    public int getSize() {
        return tags.size();
    }

    @NonNull
    public Tag getTagAt(int position) {
        return tags.get(position);
    }

    @NonNull
    public Observable<Tag> addTag(long tagId) {
        return tagsModel.getById(tagId).doOnNext(addTagToList());
    }

    public void removeAt(int position) {
        tags.remove(position);
    }

    public List<Tag> getAll() {
        return tags;
    }

    @NonNull
    private Action1<Tag> addTagToList() {
        return new Action1<Tag>() {
            @Override
            public void call(Tag tag) {
                tags.add(tag);
            }
        };
    }
}
