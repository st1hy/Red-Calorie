package com.github.st1hy.countthemcalories.ui.core.tokensearch;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public interface Searchable {

    void setOnSearchChanged(@Nullable OnSearchChanged onSearchChanged);

    @NonNull
    String getQuery();

    @NonNull
    List<String> getTokens();

}
