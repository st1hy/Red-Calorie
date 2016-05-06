package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.presenter.DrawerPresenter;

import rx.Observable;

public interface IngredientsPresenter extends DrawerPresenter {

    @Override
    void onStart();

    void onStop();

    /**
     * Cache, created in onCreateMenu
     */
    void onSearch(@NonNull Observable<CharSequence> observable);
}
