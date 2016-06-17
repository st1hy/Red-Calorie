package com.github.st1hy.countthemcalories.core.tokensearch;

import android.support.annotation.NonNull;

import java.util.List;

public class SearchResult {
    final String query;
    final List<String> tokens;

    public SearchResult(@NonNull String query, @NonNull List<String> tokens) {
        this.query = query;
        this.tokens = tokens;
    }

    @NonNull
    public String getQuery() {
        return query;
    }

    @NonNull
    public List<String> getTokens() {
        return tokens;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "query='" + query + '\'' +
                ", tokens=" + tokens +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchResult that = (SearchResult) o;
        return query.equals(that.query) && tokens.equals(that.tokens);
    }

    @Override
    public int hashCode() {
        int result = query.hashCode();
        result = 31 * result + tokens.hashCode();
        return result;
    }
}
