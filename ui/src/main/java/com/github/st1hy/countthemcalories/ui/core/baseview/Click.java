package com.github.st1hy.countthemcalories.ui.core.baseview;

import android.support.annotation.NonNull;

public final class Click {

    private static final Click INSTANCE = new Click();

    private Click() {
    }

    @NonNull
    public static Click get() {
        return INSTANCE;
    }
}
