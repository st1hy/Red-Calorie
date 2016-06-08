package com.github.st1hy.countthemcalories.core.command.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import rx.Observable;

public interface UndoView {

    @NonNull
    Observable<Void> showUndoMessage(@StringRes int undoMessageResId);

    void hideUndoMessage();
}
