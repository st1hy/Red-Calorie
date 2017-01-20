package com.github.st1hy.countthemcalories.activities.tags.fragment.view;

import android.support.annotation.NonNull;
import android.view.View;

import com.jakewharton.rxbinding.internal.Functions;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;
import javax.inject.Named;

public class ShowHideAnimation {

    @NonNull
    private final View view;

    @Inject
    public ShowHideAnimation(@NonNull @Named("animated") View view) {
        this.view = view;
    }

    public void show() {
        view.animate().translationY(0)
                .setDuration(200)
                .start();
    }

    public void hide() {
        if (view.getHeight() == 0) {
            RxView.preDraws(view, Functions.FUNC0_ALWAYS_TRUE)
                    .first().subscribe(aVoid -> hide());
        } else {
            view.animate().translationY(view.getHeight() * 2)
                    .setDuration(200)
                    .start();
        }
    }

    @NonNull
    public View getView() {
        return view;
    }
}
