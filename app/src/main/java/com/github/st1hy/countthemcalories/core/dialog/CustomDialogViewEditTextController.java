package com.github.st1hy.countthemcalories.core.dialog;

import android.app.AlertDialog;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.jakewharton.rxbinding.widget.RxTextView;

import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

public final class CustomDialogViewEditTextController {

    private CustomDialogViewEditTextController() {}

    public static Observable<String> editTextValueOnOk(@NonNull RxAlertDialog rxAlertDialog,
                                                       @NonNull String initialText,
                                                       @IdRes int customEditTextViewId) {

        final EditText text = (EditText) checkNotNull(rxAlertDialog.getCustomView())
                .findViewById(customEditTextViewId);
        text.setText(initialText);
        text.setOnKeyListener((v, keyCode, event) -> {
            if (isEnterClicked(event)) {
                finishDialog(rxAlertDialog);
            }
            return false;
        });
        text.setSelection(text.length());
        RxTextView.editorActions(text)
                .filter(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(actionId1 -> finishDialog(rxAlertDialog));
        return rxAlertDialog.observePositiveClick()
                .map(aVoid -> text.getText().toString());
    }

    private static boolean finishDialog(@NonNull RxAlertDialog rxAlertDialog) {
        return rxAlertDialog.getDialog()
                .getButton(AlertDialog.BUTTON_POSITIVE)
                .performClick();
    }

    private static boolean isEnterClicked(KeyEvent event) {
        return event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
    }
}
