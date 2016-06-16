package com.github.st1hy.countthemcalories.core.tokensearch.view;

import android.support.annotation.NonNull;

import java.util.List;

public interface SearchChanged {

    void onSearching(@NonNull String query, @NonNull List<String> tokens);
}
