package com.github.st1hy.countthemcalories.core;

import android.os.Build;

import javax.inject.Inject;

public class Utils {

    @Inject
    public Utils() {
    }

    public boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
