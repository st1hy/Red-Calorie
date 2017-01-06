package com.github.st1hy.countthemcalories.core.tokensearch;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;
import javax.inject.Provider;

@PerFragment
public class LastSearchResult implements Provider<SearchResult> {

    private SearchResult lastResult = SearchResult.EMPTY;

    @Inject
    public LastSearchResult() {
    }

    public void set(@NonNull SearchResult lastResult) {
        this.lastResult = lastResult;
    }

    @Override
    public SearchResult get() {
        return lastResult;
    }
}
