package com.github.st1hy.countthemcalories.ui.activities.tags.model;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

import javax.inject.Inject;

@PerFragment
public class TagsViewModel {

    private final Resources resources;

    @Inject
    TagsViewModel(@NonNull Resources resources) {
        this.resources = resources;
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

    @NonNull
    public String getCounterFor(int ingredientCount) {
        return resources.getString(R.string.tags_ingredient_counter, ingredientCount);
    }
}
