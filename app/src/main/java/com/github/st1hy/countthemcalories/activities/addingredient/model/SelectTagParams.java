package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.support.annotation.NonNull;

import java.util.Collection;

public class SelectTagParams {

    @NonNull
    private final Collection<String> excludedTags;

    private SelectTagParams(@NonNull Collection<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    public static SelectTagParams of(@NonNull Collection<String> excludedTags) {
        return new SelectTagParams(excludedTags);
    }

    @NonNull
    public Collection<String> getExcludedTags() {
        return excludedTags;
    }
}
