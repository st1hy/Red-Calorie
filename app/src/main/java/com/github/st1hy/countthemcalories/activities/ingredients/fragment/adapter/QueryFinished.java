package com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;

public class QueryFinished {
    private final Cursor cursor;
    private final SearchResult searchingFor;

    private QueryFinished(@NonNull Cursor cursor, @NonNull SearchResult searchingFor) {
        this.cursor = cursor;
        this.searchingFor = searchingFor;
    }

    public static QueryFinished of(@NonNull Cursor cursor, @NonNull SearchResult searchingFor) {
        return new QueryFinished(cursor, searchingFor);
    }

    @NonNull
    public Cursor getCursor() {
        return cursor;
    }

    @NonNull
    public SearchResult getSearchingFor() {
        return searchingFor;
    }
}
