package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.support.annotation.NonNull;

import java.util.Collection;

public class SelectTagParams {

    @NonNull
    private final Collection<String> excludedTags;

    public SelectTagParams(@NonNull Collection<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    @NonNull
    public Collection<String> getExcludedTags() {
        return excludedTags;
    }
}
