package com.github.st1hy.countthemcalories.activities.tags.presenter;

import android.support.annotation.NonNull;

import rx.Observable;

public interface TagsPresenter {
    void onStart();

    void onStop();

    /**
     * Cache, its provided by onCreateOptionsMenu
     */
    void onSearch(@NonNull Observable<CharSequence> observable);
}
