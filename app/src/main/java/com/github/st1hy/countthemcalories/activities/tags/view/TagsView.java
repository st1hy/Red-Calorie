package com.github.st1hy.countthemcalories.activities.tags.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.core.baseview.DialogView;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;
import com.github.st1hy.countthemcalories.core.state.Visibility;

import rx.Observable;

public interface TagsView extends DialogView, DrawerView {
    /**
     * @return observable emitting new tag name to add
     */
    @NonNull
    Observable<String> showEditTextDialog(@StringRes int newTagDialogTitle, @NonNull String initialText);

    void setNoTagsButtonVisibility(@NonNull  Visibility visibility);

    /**
     * @return observable emitting onNext when user clicks ok to remove
     */
    @NonNull
    Observable<Void> showRemoveTagDialog();

    void scrollToPosition(int position);

    void setResultAndReturn(long tagId, @NonNull String tagName);

    @NonNull
    Observable<Void> getOnAddTagClickedObservable();

    @NonNull
    Observable<Void> showUndoMessage(@StringRes int undoMessageResId);

    void hideUndoMessage();
}
