package com.github.st1hy.countthemcalories.activities.ingredients.view.searchview;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Query implements Serializable {
    @NonNull
    private final String query;

    public Query(@NonNull String query) {
        this.query = query;
    }

    @NonNull
    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return query;
    }
}
