package com.github.st1hy.countthemcalories.core;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkArgument;

public class Utils {

    @Inject
    public Utils() {
    }

    @NonNull
    public static <T> T assertNotNull(@Nullable T t) {
        if (t == null) throw new NullPointerException();
        return t;
    }

    public static <T> T checkIsSubclass(@NonNull Activity activity, @NonNull Class<T> tClass) {
        checkArgument(tClass.isInstance(activity), "activity must implement " + tClass);
        return tClass.cast(activity);
    }

    public boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
