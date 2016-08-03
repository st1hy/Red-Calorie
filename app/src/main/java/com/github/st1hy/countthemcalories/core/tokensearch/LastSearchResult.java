package com.github.st1hy.countthemcalories.core.tokensearch;

import android.support.annotation.NonNull;

import javax.inject.Provider;

public class LastSearchResult implements Provider<SearchResult> {

    private SearchResult lastResult = SearchResult.EMPTY;

    public void set(@NonNull SearchResult lastResult) {
        this.lastResult = lastResult;
    }

    @Override
    public SearchResult get() {
        return lastResult;
    }
}
