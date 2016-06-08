package com.github.st1hy.countthemcalories.core.command.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerActivity;
import com.github.st1hy.countthemcalories.core.rx.RxSnackbar;

import rx.Observable;

public abstract class UndoDrawerActivity extends DrawerActivity implements UndoView {

    Snackbar undo = null;

    @NonNull
    @Override
    public Observable<Void> showUndoMessage(@StringRes int undoMessageResId) {
        RxSnackbar rxSnackbar = RxSnackbar.make(getUndoRoot(), undoMessageResId, Snackbar.LENGTH_LONG);
        Observable<Void> observable = rxSnackbar.action(R.string.undo);
        undo = rxSnackbar.getSnackbar();
        undo.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                undo = null;
            }
        });
        rxSnackbar.show();
        return observable;
    }

    @Override
    public void hideUndoMessage() {
        if (undo != null) {
            undo.dismiss();
            undo = null;
        }
    }

    @NonNull
    protected abstract View getUndoRoot();
}
