package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.support.annotation.NonNull;

import java.util.List;

public interface SearchSuggestionsView {

    void expandSearchBar();

    void setSearchQuery(@NonNull String query, @NonNull List<String> tokens);
}
