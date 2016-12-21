package com.github.st1hy.countthemcalories.core.command.undo;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import rx.Observable;

public interface UndoView {

    @CheckResult
    @NonNull
    Observable<UndoAction> showUndoMessage(@StringRes int undoMessageResId);

    void hideUndoMessage();
}
