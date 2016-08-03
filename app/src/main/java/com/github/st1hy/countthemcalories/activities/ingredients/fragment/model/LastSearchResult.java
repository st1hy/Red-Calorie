package com.github.st1hy.countthemcalories.activities.ingredients.fragment.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;

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
