package com.github.st1hy.countthemcalories.activities.addingredient.fragment.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.Tag;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Parcel
public class IngredientTagsModel {

    public static final String SAVED_TAGS_MODEL = "add ingredient tags model";

    private static final Function<Tag, Long> TAG_TO_ID = Tag::getId;
    private static final Function<Tag, String> TAG_TO_NAME = Tag::getName;

    final ArrayList<Tag> tags;

    @ParcelConstructor
    public IngredientTagsModel(@NonNull ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public int getSize() {
        return tags.size();
    }

    @NonNull
    public Tag getTagAt(int position) {
        return tags.get(position);
    }

    public int addTag(@NonNull Tag tag) {
        tags.add(tag);
        return tags.size() - 1;
    }

    public int remove(@NonNull Tag tag) {
        int index = tags.indexOf(tag);
        tags.remove(tag);
        return index;
    }

    @NonNull
    public Collection<String> getTagNames() {
        return Collections2.transform(tags, TAG_TO_NAME);
    }

    @NonNull
    Collection<Long> getTagIds() {
        return Collections2.transform(tags, TAG_TO_ID);
    }

}
