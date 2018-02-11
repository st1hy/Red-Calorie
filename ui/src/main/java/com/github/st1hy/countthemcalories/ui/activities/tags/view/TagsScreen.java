package com.github.st1hy.countthemcalories.ui.activities.tags.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.tags.model.Tags;
import com.github.st1hy.countthemcalories.ui.core.baseview.Click;
import com.github.st1hy.countthemcalories.ui.core.state.Visibility;

import rx.Observable;

public interface TagsScreen {

    void openIngredientsFilteredBy(@NonNull String tagName);

    void onTagsSelected(@NonNull Tags tag);

    @NonNull
    @CheckResult
    Observable<Click> addTagClickedObservable();

    @NonNull
    @CheckResult
    Observable<CharSequence> getQueryObservable();

    void setConfirmButtonVisibility(@NonNull Visibility visibility);

    @NonNull
    @CheckResult
    Observable<Click> confirmClickedObservable();
}
