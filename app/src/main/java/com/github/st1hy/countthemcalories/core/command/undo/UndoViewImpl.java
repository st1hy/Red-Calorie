package com.github.st1hy.countthemcalories.core.command.undo;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.RxSnackbar;

import rx.Observable;

public final class UndoViewImpl implements UndoView {

    @NonNull
    private final View rootView;

    private Snackbar undo;

    public UndoViewImpl(@NonNull View rootView) {
        this.rootView = rootView;
    }

    @Override
    @NonNull
    public Observable<UndoAction> showUndoMessage(@StringRes int undoMessageResId) {
        RxSnackbar rxSnackbar = RxSnackbar.make(rootView, undoMessageResId, Snackbar.LENGTH_LONG);
        Observable<Void> observable = rxSnackbar.action(R.string.undo);
        undo = rxSnackbar.getSnackbar();
        undo.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                undo = null;
            }
        });
        rxSnackbar.show();
        return observable.map(Functions.into(UndoAction.UNDO));
    }

    @Override
    public void hideUndoMessage() {
        if (undo != null) {
            undo.dismiss();
            undo = null;
        }
    }
}
