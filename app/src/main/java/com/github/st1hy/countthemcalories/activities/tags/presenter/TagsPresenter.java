package com.github.st1hy.countthemcalories.activities.tags.presenter;

import android.support.annotation.NonNull;

import rx.Observable;

public interface TagsPresenter {
    void onAddTagClicked(@NonNull Observable<Void> clicks);

    void onStart();

    void onStop();

    void onRefresh(@NonNull Observable<Void> refreshes);
}
