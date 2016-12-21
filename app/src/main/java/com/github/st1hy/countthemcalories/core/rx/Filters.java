package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;

import rx.functions.Func1;

public class Filters {

    public static <T> Func1<T, Boolean> equalTo(@NonNull final T required) {
        return required::equals;
    }
}
