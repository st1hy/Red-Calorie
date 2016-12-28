package com.github.st1hy.countthemcalories.shadows;


import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.internal.ShadowExtractor;


@SuppressWarnings({"UnusedDeclaration", "Unchecked"})
@Implements(Snackbar.class)
public class ShadowSnackbar {
    static ShadowSnackbar lastSnackbar;

    @RealObject
    Snackbar snackbar;

    String text;
    private View.OnClickListener listener;

    public void __constructor__(ViewGroup viewGroup) {

    }

    @Implementation
    @NonNull
    public Snackbar setText(@NonNull CharSequence message) {
        this.text = message.toString();
        return snackbar;
    }

    @Implementation
    public Snackbar setDuration(@Snackbar.Duration int duration) {
        return snackbar;
    }

    @Implementation
    public Snackbar setAction(@StringRes int resId, View.OnClickListener listener) {
        this.listener = listener;
        return snackbar;
    }

    @Implementation
    public static Snackbar make(@NonNull View view, @StringRes int resId, int duration) {
        Snackbar snackbar = Snackbar.make(view, view.getResources().getText(resId), duration);
        lastSnackbar = shadowOf(snackbar);
        return snackbar;
    }

    public void performAction() {
        listener.onClick(null);
    }

    static ShadowSnackbar shadowOf(Snackbar bar) {
        return (ShadowSnackbar) ShadowExtractor.extract(bar);
    }

    public static void reset() {
        lastSnackbar = null;
    }

    public static String getTextOfLatestSnackbar() {
        if (lastSnackbar != null)
            return lastSnackbar.text;

        return null;
    }

    public static Snackbar getLatestSnackbar() {
        if (lastSnackbar != null)
            return lastSnackbar.snackbar;

        return null;
    }

    public static ShadowSnackbar getLatest() {
        return lastSnackbar;
    }
}
