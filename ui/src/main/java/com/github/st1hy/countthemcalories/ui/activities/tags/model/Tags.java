package com.github.st1hy.countthemcalories.ui.activities.tags.model;

import com.github.st1hy.countthemcalories.ui.contract.Tag;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.List;

@Parcel
public class Tags {

    final List<Tag> tags;

    @ParcelConstructor
    public Tags(List<Tag> tags) {
        this.tags = ImmutableList.copyOf(tags);
    }

    public List<Tag> getTags() {
        return tags;
    }

    public boolean isEmpty() {
        return tags.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tags tags1 = (Tags) o;

        return tags.equals(tags1.tags);
    }

    @Override
    public int hashCode() {
        return tags.hashCode();
    }

    @Override
    public String toString() {
        return "Tags: " + Iterables.toString(tags);
    }
}
