package com.github.st1hy.countthemcalories.activities.tags.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.ui.Visibility;
import com.github.st1hy.countthemcalories.core.ui.view.DialogView;

import rx.Observable;

public interface TagsView extends DialogView {
    /**
     * @return observable emitting new tag name to add
     */
    @NonNull
    Observable<String> showEditTextDialog(int newTagDialogTitle);

    void setNoTagsButtonVisibility(@NonNull  Visibility visibility);

    void setDataRefreshing(boolean isRefreshing);

    /**
     * @return observable emitting onNext when user clicks ok to remove
     */
    @NonNull
    Observable<Void> showRemoveTagDialog();

    void scrollToPosition(int position);

    void setResultAndReturn(long tagId);
}
