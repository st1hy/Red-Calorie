package com.github.st1hy.countthemcalories.activities.tags.model;

import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;

public class TagsViewModel {

    @StringRes
    public int getNewTagDialogTitle() {
        return R.string.tags_new_tag_dialog;
    }

    @StringRes
    public int getUndoDeleteMessage() {
        return R.string.tags_undo_delete_message;
    }

    @StringRes
    public int getUndoAddMessage() {
        return R.string.tags_undo_add_message;
    }
}
