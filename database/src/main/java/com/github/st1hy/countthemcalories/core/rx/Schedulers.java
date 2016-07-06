package com.github.st1hy.countthemcalories.core.rx;

import rx.Scheduler;

public class Schedulers {
    private static final Hook DEFAULT = new HookImp();
    private static Hook hook = DEFAULT;

    public static Scheduler io() {
        return hook.io();
    }

    public static Scheduler computation() {
        return hook.computation();
    }

    public static Scheduler immediate() {
        return hook.immediate();
    }


    public static void registerHook(Hook hook) {
        Schedulers.hook = hook;
    }

    public static void reset() {
        Schedulers.hook = DEFAULT;
    }

    public interface Hook {
        Scheduler io();
        Scheduler computation();
        Scheduler immediate();
    }

    public static class HookImp implements Hook {
        @Override
        public Scheduler io() {
            return rx.schedulers.Schedulers.io();
        }

        @Override
        public Scheduler computation() {
            return rx.schedulers.Schedulers.computation();
        }

        @Override
        public Scheduler immediate() {
            return rx.schedulers.Schedulers.immediate();
        }
    }
}
