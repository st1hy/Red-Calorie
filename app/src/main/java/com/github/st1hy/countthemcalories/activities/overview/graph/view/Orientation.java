package com.github.st1hy.countthemcalories.activities.overview.graph.view;

import android.support.annotation.NonNull;

enum Orientation {
    RIGHT_TO_LEFT(1), LEFT_TO_RIGHT(2), TOP_TO_BOTTOM(3), BOTTOM_TO_TOP(4);

    final int gc_orientation;

    Orientation(int gc_orientation) {
        this.gc_orientation = gc_orientation;
    }

    @NonNull
    public static Orientation valueOf(int gc_orientation) throws IllegalArgumentException {
        for (Orientation o : values()) {
            if (o.gc_orientation == gc_orientation) return o;
        }
        throw new IllegalArgumentException("Unknown orientation value");
    }
}
