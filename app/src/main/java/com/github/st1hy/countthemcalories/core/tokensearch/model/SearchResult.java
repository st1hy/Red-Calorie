package com.github.st1hy.countthemcalories.core.tokensearch.model;

import android.support.annotation.NonNull;

import java.util.List;

public class SearchResult {
    final String ingredientName;
    final List<String> tags;

    public SearchResult(@NonNull String ingredientName, @NonNull List<String> tags) {
        this.ingredientName = ingredientName;
        this.tags = tags;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public List<String> getTags() {
        return tags;
    }
}
