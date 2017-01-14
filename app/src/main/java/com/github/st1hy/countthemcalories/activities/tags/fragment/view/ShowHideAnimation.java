package com.github.st1hy.countthemcalories.activities.tags.fragment.view;

import android.support.annotation.NonNull;
import android.view.View;

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
        view.setVisibility(View.VISIBLE);
        view.animate().translationY(0)
                .setDuration(200)
                .start();
    }

    public void hide() {
        view.animate().translationY(view.getHeight())
                .setDuration(200)
                .start();
    }

    @NonNull
    public View getView() {
        return view;
    }
}
