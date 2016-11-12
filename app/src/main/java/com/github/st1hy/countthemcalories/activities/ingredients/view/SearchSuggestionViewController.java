package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;

import java.util.List;

import javax.inject.Inject;

public class SearchSuggestionViewController implements SearchSuggestionsView {

    @NonNull
    private final TokenSearchView searchView;

    @Inject
    public SearchSuggestionViewController(@NonNull TokenSearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    public void expandSearchBar() {
        searchView.expand(false);
    }


    @Override
    public void setSearchQuery(@NonNull String query, @NonNull List<String> tokens) {
        searchView.setQuery(query, tokens);
    }
}
