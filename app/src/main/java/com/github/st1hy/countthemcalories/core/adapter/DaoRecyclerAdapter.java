package com.github.st1hy.countthemcalories.core.adapter;

import android.support.annotation.NonNull;

import rx.Observable;

public interface DaoRecyclerAdapter {

    void onStart();

    void onStop();

    /**
     * Cache, created in onCreateMenu
     */
    void onSearch(@NonNull Observable<CharSequence> observable);
}
