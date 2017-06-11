package com.github.st1hy.countthemcalories.core.dialog;

import android.app.AlertDialog;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.core.rx.RxInputMethodManager;
import com.jakewharton.rxbinding.internal.Functions;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import rx.Observable;

import static com.github.st1hy.countthemcalories.core.inject.InputMethodManagerModule.inputMethodManager;
import static com.google.common.base.Preconditions.checkNotNull;

public final class CustomDialogViewEditTextController {

    private CustomDialogViewEditTextController() {
    }

    @CheckResult
    @NonNull
    public static Observable<String> editTextValueOnOk(@NonNull RxAlertDialog rxAlertDialog,
                                                       @NonNull String initialText,
                                                       @IdRes int customEditTextViewId) {
        final EditText text = checkNotNull(rxAlertDialog.getCustomView())
                .findViewById(customEditTextViewId);
        text.setText(initialText);
        text.setOnKeyListener((v, keyCode, event) -> {
            if (isEnterClicked(event)) {
                finishDialog(rxAlertDialog);
            }
            return false;
        });
        text.setSelection(text.length());
        showSoftInputUntilItsShown(text);
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

    public static void showSoftInputUntilItsShown(View view) {
        final InputMethodManager inputMethodManager = inputMethodManager(view.getContext());
        RxView.preDraws(view, Functions.FUNC0_ALWAYS_TRUE)
                .flatMap(aVoid -> RxInputMethodManager.showSoftInput(inputMethodManager, view, 0))
                .filter(result -> result == InputMethodManager.RESULT_SHOWN)
                .first()
                .subscribe();
    }
}
