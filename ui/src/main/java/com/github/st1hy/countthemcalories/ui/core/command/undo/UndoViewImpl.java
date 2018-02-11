package com.github.st1hy.countthemcalories.ui.core.command.undo;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.core.command.undo.inject.UndoRootView;
import com.github.st1hy.countthemcalories.ui.core.rx.Functions;
import com.github.st1hy.countthemcalories.ui.core.rx.RxSnackbar;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;

import javax.inject.Inject;

import rx.Observable;

@PerActivity
public final class UndoViewImpl implements UndoView {

    @NonNull
    private final View rootView;

    private Snackbar undo;

    @Inject
    public UndoViewImpl(@NonNull @UndoRootView View rootView) {
        this.rootView = rootView;
    }

    @CheckResult
    @Override
    @NonNull
    public Observable<UndoAction> showUndoMessage(@StringRes int undoMessageResId) {
        RxSnackbar rxSnackbar = RxSnackbar.make(rootView, undoMessageResId, Snackbar.LENGTH_LONG);
        Observable<Void> observable = rxSnackbar.action(R.string.undo);
        undo = rxSnackbar.getSnackbar();
        undo.addCallback(new Snackbar.Callback() {
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
