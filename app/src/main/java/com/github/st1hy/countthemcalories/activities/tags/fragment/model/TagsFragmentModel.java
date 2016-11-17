package com.github.st1hy.countthemcalories.activities.tags.fragment.model;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.fragment.TagsFragment;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class TagsFragmentModel {

    final Bundle arguments;

    public TagsFragmentModel(@NonNull Bundle arguments) {
        this.arguments = arguments;
    }

    public boolean isInSelectMode() {
        return arguments.getBoolean(TagsFragment.ARG_PICK_BOOL, false);
    }

    @NonNull
    public Collection<String> getExcludedTagIds() {
        String[] tags = arguments.getStringArray(TagsFragment.ARG_EXCLUDED_TAGS_STRING_ARRAY);
        if (tags != null) {
            return Arrays.asList(tags);
        }
        return Collections.emptyList();
    }
}
