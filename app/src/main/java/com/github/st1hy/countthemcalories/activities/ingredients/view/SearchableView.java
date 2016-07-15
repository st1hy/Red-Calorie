package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;

import rx.Observable;

public interface SearchableView {

    @NonNull
    Observable<SearchResult> getSearchObservable();
}
