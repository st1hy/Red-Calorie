package com.github.st1hy.countthemcalories.activities.tags.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.Tag;

import rx.Observable;

public interface TagsScreen {

    void openIngredientsFilteredBy(@NonNull String tagName);

    void onTagSelected(@NonNull Tag tag);

    @NonNull
    @CheckResult
    Observable<Void> getAddTagClickedObservable();

    @NonNull
    @CheckResult
    Observable<CharSequence> getQueryObservable();
}
