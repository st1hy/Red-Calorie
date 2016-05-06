package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import com.github.st1hy.countthemcalories.core.ui.presenter.DrawerPresenter;

import rx.Observable;

public interface IngredientsPresenter extends DrawerPresenter {

    @Override
    void onStart();

    void onStop();

    /**
     * Cache, created in onCreateMenu
     */
    void onSearch(Observable<CharSequence> observable);
}
