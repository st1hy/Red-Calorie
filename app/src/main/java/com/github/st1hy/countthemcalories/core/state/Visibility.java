package com.github.st1hy.countthemcalories.core.state;

import android.view.View;

public enum Visibility {
    VISIBLE(View.VISIBLE), GONE(View.GONE), INVISIBLE(View.INVISIBLE)
    ;
    private final int visibility;

    Visibility(int visibility) {
        this.visibility = visibility;
    }

    public int getVisibility() {
        return visibility;
    }

    public static Visibility of(boolean isVisible) {
        return isVisible ? VISIBLE : GONE;
    }
}
