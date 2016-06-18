package com.github.st1hy.countthemcalories.activities.tags.model;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

public class TagsActivityModel {
    private final Intent intent;

    @Inject
    public TagsActivityModel(@Nullable Intent intent) {
        this.intent = intent;
    }

    public boolean isInSelectMode() {
        return intent != null && TagsActivity.ACTION_PICK_TAG.equals(intent.getAction());
    }

    @NonNull
    public Collection<String> getExcludedTagIds() {
        if (intent != null) {
            String[] tags = intent.getStringArrayExtra(TagsActivity.EXTRA_EXCLUDE_TAG_STRING_ARRAY);
            if (tags != null) {
                return Arrays.asList(tags);
            }
        }
        return Collections.emptyList();
    }
}
