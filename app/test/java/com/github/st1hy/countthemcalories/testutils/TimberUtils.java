package com.github.st1hy.countthemcalories.testutils;

import android.util.Log;

import timber.log.Timber;

public class TimberUtils {

    public static final Timber.Tree ALL = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            System.out.println(message);
        }
    };

    public static final Timber.Tree ABOVE_WARN = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority >= Log.WARN) {
                String prefix = "";
                switch (priority) {
                    case Log.WARN:
                        prefix = "WARN: ";
                        break;
                    case Log.ERROR:
                        prefix = "ERROR: ";
                        break;
                    case Log.ASSERT:
                        prefix = "ASSERT: ";
                        break;
                }
                System.err.println(prefix + message);
            }
        }
    };
}
