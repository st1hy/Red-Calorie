package com.github.st1hy.countthemcalories.core.adapter;

import android.support.annotation.NonNull;

public class RecyclerEvent {
    private final Type type;
    private final int position;

    public RecyclerEvent(@NonNull Type type, int position) {
        this.type = type;
        this.position = position;
    }

    @NonNull
    public static RecyclerEvent create(@NonNull Type type, int position) {
        return new RecyclerEvent(type, position);
    }

    @NonNull
    public Type getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public enum Type {
        ADDED, REMOVED
    }
}
