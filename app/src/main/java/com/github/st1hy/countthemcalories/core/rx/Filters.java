package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;

import rx.functions.Func1;

public class Filters {

    public static <T> Func1<T, Boolean> equalTo(@NonNull final T required) {
        return new Func1<T, Boolean>() {
            @Override
            public Boolean call(T t) {
                return required.equals(t);
            }
        };
    }
}
