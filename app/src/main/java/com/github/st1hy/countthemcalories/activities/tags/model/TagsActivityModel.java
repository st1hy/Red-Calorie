package com.github.st1hy.countthemcalories.activities.tags.model;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;

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
}
