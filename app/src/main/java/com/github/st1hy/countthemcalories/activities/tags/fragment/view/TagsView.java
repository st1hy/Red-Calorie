package com.github.st1hy.countthemcalories.activities.tags.fragment.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.activities.tags.view.TagsScreen;
import com.github.st1hy.countthemcalories.core.state.Visibility;

import rx.Observable;

public interface TagsView extends TagsScreen {
    /**
     * @return observable emitting new tag name to add
     */
    @NonNull
    Observable<String> showEditTextDialog(@StringRes int newTagDialogTitle, @NonNull String initialText);

    void setNoTagsVisibility(@NonNull  Visibility visibility);

    /**
     * @return observable emitting onNext when user clicks ok to remove
     */
    @NonNull
    Observable<Void> showRemoveTagDialog();

    void scrollToPosition(int position);


    void setNoTagsMessage(@StringRes int messageResId);

}
