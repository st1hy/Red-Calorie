package com.github.st1hy.countthemcalories.activities.tags.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.ui.Visibility;
import com.github.st1hy.countthemcalories.core.ui.view.DialogView;

import rx.Observable;

public interface TagsView extends DialogView {
    @NonNull
    Observable<String> showEditTextDialog(int newTagDialogTitle);

    void setNoTagsButtonVisibility(@NonNull  Visibility visibility);

    void setDataRefreshing(boolean isRefreshing);
}
