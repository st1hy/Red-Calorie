package com.github.st1hy.countthemcalories.core.fragments;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class FragmentLocation<T extends Fragment> {
    public static final int NO_ID = -1;

    private final Class<T> klass;
    private final String tag;
    private int rootViewId;
    private Injector<T> injector;

    private FragmentLocation(Builder<T> builder) {
        this.klass = builder.klass;
        this.tag = builder.tag;
        this.rootViewId = builder.rootViewId;
        this.injector = builder.injector;
    }

    @NonNull
    public T newFragment() {
        try {
            T t = klass.newInstance();
            maybeInject(t);
            return t;
        } catch (Exception e) {
            throw new IllegalStateException("Could not instantiate fragment", e);
        }
    }

    @NonNull
    public String getTag() {
        return tag;
    }

    public int getRootViewId() {
        return rootViewId;
    }

    @Nullable
    public T maybeInject(@Nullable T t) {
        if (t != null && injector != null) {
            injector.inject(t);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public T findFragmentIn(@NonNull FragmentManager fragmentManager) {
        T t = (T) fragmentManager.findFragmentByTag(tag);
        maybeInject(t);
        return t;
    }

    public static class Builder<T extends Fragment> {
        private final Class<T> klass;
        private final String tag;
        private int rootViewId;
        private Injector<T> injector;

        public Builder(Class<T> klass, String tag) {
            this.klass = klass;
            this.tag = tag;
        }

        public Builder<T> setViewRootId(@IdRes int viewRootId) {
            this.rootViewId = viewRootId;
            return this;
        }

        public Builder<T> setInjector(Injector<T> injector) {
            this.injector = injector;
            return this;
        }

        public FragmentLocation<T> build() {
            return new FragmentLocation<>(this);
        }
    }

    public interface Injector<T extends Fragment> {
        void inject(T t);
    }
}
