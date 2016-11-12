package com.github.st1hy.countthemcalories.activities.tags.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.command.undo.UndoViewImpl;
import com.github.st1hy.countthemcalories.database.Tag;

import rx.Observable;

public interface TagsScreen extends UndoViewImpl {

    void openIngredientsFilteredBy(@NonNull String tagName);

    void onTagSelected(@NonNull Tag tag);

    @NonNull
    Observable<Void> getAddTagClickedObservable();

    @NonNull
    Observable<CharSequence> getQueryObservable();
}
