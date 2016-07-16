package com.github.st1hy.countthemcalories.activities.tags.fragment.model;

import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;

import javax.inject.Inject;

public class TagsViewModel {

    @Inject
    public TagsViewModel() {
    }

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

    @StringRes
    public int getEditTagDialogTitle() {
        return R.string.tags_edit_tag_dialog;
    }

    @StringRes
    public int getNoTagsMessage() {
        return R.string.tags_no_tags_yet;
    }

    @StringRes
    public int getSearchResultEmptyMessage() {
        return R.string.tags_search_result_empty;
    }
}