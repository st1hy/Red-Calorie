package com.github.st1hy.countthemcalories.core.rx;

import com.github.st1hy.countthemcalories.core.command.CommandResponse;

import rx.functions.Func1;

public class Functions {

    public static final Func1<Object, Void> INTO_VOID = new Func1<Object, Void>() {
        @Override
        public Void call(Object o) {
            return null;
        }
    };
    public static final Func1<Object,Boolean> ALWAYS_FALSE = new Func1<Object, Boolean>() {
        @Override
        public Boolean call(Object o) {
            return false;
        }
    };
    public static final Func1<Object,Boolean> ALWAYS_TRUE = new Func1<Object, Boolean>() {
        @Override
        public Boolean call(Object o) {
            return true;
        }
    };

    public static <T> Func1<Object, T> into(final T t) {
        return new Func1<Object, T>() {
            @Override
            public T call(Object o) {
                return t;
            }
        };
    }

    public static <T> Func1<CommandResponse<T, ?>, T> intoResponse() {
        return new Func1<CommandResponse<T, ?>, T>() {
            @Override
            public T call(CommandResponse<T, ?> commandResponse) {
                return commandResponse.getResponse();
            }
        };
    }

}
