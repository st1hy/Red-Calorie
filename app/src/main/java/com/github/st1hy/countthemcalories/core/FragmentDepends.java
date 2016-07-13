package com.github.st1hy.countthemcalories.core;

import android.app.Activity;
import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

public final class FragmentDepends {

    private FragmentDepends() {
    }

    public static <T> T checkIsSubclass(@NonNull Activity activity, @NonNull Class<T> tClass) {
        checkArgument(tClass.isInstance(activity), "activity must implement " + tClass);
        return tClass.cast(activity);
    }
}
