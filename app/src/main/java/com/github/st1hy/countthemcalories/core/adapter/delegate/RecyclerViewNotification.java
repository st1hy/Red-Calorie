package com.github.st1hy.countthemcalories.core.adapter.delegate;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RecyclerViewNotification {
    private static final RecyclerViewNotification DATA_SET_CHANGED_NOTIFICATION = new RecyclerViewNotification(Type.DATA_SET_CHANGED, -1, -1, null);

    public enum Type {
        DATA_SET_CHANGED, ITEM_CHANGED, RANGE_CHANGED, ITEM_INSERTED, ITEM_MOVED, RANGE_INSERTED, ITEM_REMOVED, ITEM_RANGE_REMOVED
    }

    private final Type type;
    private final int position, position2;
    private final Object payload;

    RecyclerViewNotification(@NonNull Type type,
                             int position,
                             int position2,
                             @Nullable Object payload) {
        this.type = type;
        this.position = position;
        this.position2 = position2;
        this.payload = payload;
    }

    public void call(RecyclerViewNotifier notifier) {
        switch (type) {
            case DATA_SET_CHANGED:
                notifier.notifyDataSetChanged();
                break;
            case ITEM_CHANGED:
                notifier.notifyItemChanged(position, payload);
                break;
            case RANGE_CHANGED:
                notifier.notifyItemRangeChanged(position, position2, payload);
                break;
            case ITEM_INSERTED:
                notifier.notifyItemInserted(position);
                break;
            case ITEM_MOVED:
                notifier.notifyItemMoved(position, position2);
                break;
            case RANGE_INSERTED:
                notifier.notifyItemRangeInserted(position, position2);
                break;
            case ITEM_REMOVED:
                notifier.notifyItemRemoved(position);
                break;
            case ITEM_RANGE_REMOVED:
                notifier.notifyItemRangeRemoved(position, position2);
                break;
        }
    }

    @NonNull
    public static RecyclerViewNotification of(@NonNull Type type,
                                              int position, int position2,
                                              @Nullable Object payload) {
        if (type == Type.DATA_SET_CHANGED)
            return DATA_SET_CHANGED_NOTIFICATION;
        else
            return new RecyclerViewNotification(type, position, position2, payload);
    }
}
