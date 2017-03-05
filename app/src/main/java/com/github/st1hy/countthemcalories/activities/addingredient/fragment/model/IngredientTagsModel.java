package com.github.st1hy.countthemcalories.activities.addingredient.fragment.model;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.fragment.model.Tags;
import com.github.st1hy.countthemcalories.database.Tag;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.parceler.Transient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;

@Parcel
public class IngredientTagsModel {

    public static final String SAVED_TAGS_MODEL = "add ingredient tags model";

    final ArrayList<Tag> tags;

    @Transient
    private final BehaviorSubject<Integer> tagsSize;

    @ParcelConstructor
    public IngredientTagsModel(@NonNull ArrayList<Tag> tags) {
        this.tags = tags;
        tagsSize = BehaviorSubject.create(tags.size());
    }

    public int getSize() {
        return tags.size();
    }

    @NonNull
    public Tag getTagAt(int position) {
        return tags.get(position);
    }

    public void replaceWith(@NonNull Tags tags) {
        this.tags.clear();
        this.tags.addAll(tags.getTags());
        emitSize();
    }

    public int remove(@NonNull Tag tag) {
        int index = tags.indexOf(tag);
        tags.remove(tag);
        emitSize();
        return index;
    }

    private void emitSize() {
        tagsSize.onNext(this.tags.size());
    }

    @NonNull
    public List<Tag> copyTags() {
        return ImmutableList.copyOf(tags);
    }

    @NonNull
    Collection<Long> getTagIds() {
        return Collections2.transform(tags, Tag::getId);
    }

    @NonNull
    @CheckResult
    public Observable<Integer> getTagsSizeObservable() {
        return tagsSize.asObservable();
    }
}
