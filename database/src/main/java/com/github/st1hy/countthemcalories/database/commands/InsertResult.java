package com.github.st1hy.countthemcalories.database.commands;

import android.database.Cursor;
import android.support.annotation.NonNull;

public class InsertResult {

    @NonNull
    private final Cursor cursor;
    private final int newItemPositionInCursor;

    public InsertResult(@NonNull Cursor cursor, int newItemPositionInCursor) {
        this.cursor = cursor;
        this.newItemPositionInCursor = newItemPositionInCursor;
    }

    @NonNull
    public Cursor getCursor() {
        return cursor;
    }

    /**
     * @return position of new item in cursor or -1 if new added item was not present in cursor.
     * I.e it was filtered out by current filter.
     */
    public int getNewItemPositionInCursor() {
        return newItemPositionInCursor;
    }
}
