package com.github.st1hy.countthemcalories.activities.tags.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.fragment.model.Tags;
import com.github.st1hy.countthemcalories.core.baseview.Click;
import com.github.st1hy.countthemcalories.core.state.Visibility;

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
