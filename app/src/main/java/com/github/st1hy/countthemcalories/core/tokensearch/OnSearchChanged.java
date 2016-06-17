package com.github.st1hy.countthemcalories.core.tokensearch;

import android.support.annotation.NonNull;

import java.util.List;

public interface OnSearchChanged {

    void onSearching(@NonNull String query, @NonNull List<String> tokens);
}
