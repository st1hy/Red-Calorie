package com.github.st1hy.countthemcalories.activities.tags.model;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    public Collection<Long> getExcludedTagIds() {
        if (intent != null) {
            long[] excludedIds = intent.getLongArrayExtra(TagsActivity.EXTRA_EXCLUDE_TAG_IDS);
            if (excludedIds != null) {
                List<Long> idsList = new ArrayList<>(excludedIds.length);
                for (long l : excludedIds) {
                    idsList.add(l);
                }
                return idsList;
            }
        }
        return Collections.emptyList();
    }
}
