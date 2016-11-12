package com.github.st1hy.countthemcalories.core.command.undo;

import android.support.annotation.StringRes;

import rx.Observable;

public interface UndoView {

    Observable<UndoAction> showUndoMessage(@StringRes int undoMessageResId);

    void hideUndoMessage();
}
