package com.github.st1hy.countthemcalories.core.tokensearch.model;

import android.support.annotation.NonNull;

import java.util.List;

public class SearchResult {
    final String query;
    final List<String> tokens;

    public SearchResult(@NonNull String query, @NonNull List<String> tokens) {
        this.query = query;
        this.tokens = tokens;
    }

    public String getQuery() {
        return query;
    }

    public List<String> getTokens() {
        return tokens;
    }
}
