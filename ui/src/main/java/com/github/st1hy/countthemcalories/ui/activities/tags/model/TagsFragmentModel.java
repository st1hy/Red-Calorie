package com.github.st1hy.countthemcalories.ui.activities.tags.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.contract.Tag;
import com.google.common.collect.ImmutableList;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.Set;

@Parcel
public class TagsFragmentModel {

    final boolean isInSelectMode;
    final Set<Tag> selectedTags;

    @ParcelConstructor
    public TagsFragmentModel(boolean isInSelectMode,
                             Set<Tag> selectedTags) {
        this.isInSelectMode = isInSelectMode;
        this.selectedTags = selectedTags;
    }

    public boolean isInSelectMode() {
        return isInSelectMode;
    }

    @NonNull
    public Tags getTags() {
        return new Tags(ImmutableList.copyOf(selectedTags));
    }

    public boolean isSelected(@NonNull Tag tag) {
        return selectedTags.contains(tag);
    }

    public void setSelected(@NonNull Tag tag, boolean isSelected) {
        if (isSelected) {
            if (!selectedTags.contains(tag)) {
                selectedTags.add(new Tag(tag));
            }
        } else {
            selectedTags.remove(tag);
        }
    }
}
