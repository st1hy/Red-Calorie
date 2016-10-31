package com.github.st1hy.countthemcalories.activities.addingredient.fragment.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    private static final Function<Tag, Long> TAG_TO_ID = new Function<Tag, Long>() {
        @Override
        public Long apply(Tag input) {
            return input.getId();
        }
    };
    private static final Function<Tag, String> TAG_TO_NAME = new Function<Tag, String>() {
        @Nullable
        @Override
        public String apply(Tag input) {
            return input.getName();
        }
    };

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

    public int addTag(long tagId, @NonNull String tagName) {
        tags.add(new Tag(tagId, tagName));
        return tags.size() - 1;
    }

    //Called only when loading tags from existing Ingredient
    void replaceTags(@NonNull Collection<Tag> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
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
