package com.github.st1hy.countthemcalories.core.rx;

import com.github.st1hy.countthemcalories.core.command.CommandResponse;

import rx.functions.Func1;

public class Functions {

    public static final Func1<Object, Void> INTO_VOID = o -> null;
    public static final Func1<Object, Boolean> NOT_NULL = o -> o != null;

    public static <T> Func1<Object, T> into(final T t) {
        return o -> t;
    }

    public static <T> Func1<CommandResponse<T, ?>, T> intoResponse() {
        return CommandResponse::getResponse;
    }

}
