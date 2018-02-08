package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.view.MotionEvent;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.tags.fragment.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.google.common.base.Optional;
import com.jakewharton.rxbinding.view.RxView;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

@PerActivity
@SuppressWarnings("Guava")
public class IngredientsPresenter implements BasicLifecycle {

    @Inject
    @Named("suggestions")
    SearchSuggestionsAdapter suggestionsAdapter;
    @Inject
    DrawerPresenter drawerPresenter;
    @Inject
    TokenSearchView view;
    @Inject
    Observable<SearchResult> searchResultObservable;
    @Inject
    Optional<String> filter;
    @Inject
    @Named("touchOverlay")
    View touchOverlay;
    @Inject
    RxTagsDatabaseModel databaseModel;

    private boolean isDropDownVisible;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public IngredientsPresenter() {
    }

    @Override
    public void onStart() {
        drawerPresenter.onStart();

        subscriptions.add(searchResultObservable
                .flatMap(searchResult -> {
                    if (searchResult.getQuery().trim().length() > 0)
                        return databaseModel.getAllFiltered(searchResult.getQuery(), searchResult.getTokens());
                    else return Observable.just(null);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(suggestionsAdapter::changeCursor)
                .map(cursor -> cursor != null ? cursor.getCount() : 0)
                .subscribe(view::onFilterComplete)
        );
        subscriptions.add(
                view.dropDownChange().subscribe(visible -> isDropDownVisible = visible)
        );
        subscriptions.add(
                RxView.touches(touchOverlay,
                        (event1) -> isDropDownVisible
                                && event1.getAction() == MotionEvent.ACTION_DOWN
                                || event1.getAction() == MotionEvent.ACTION_UP)
                        .filter(event -> event.getAction() == MotionEvent.ACTION_UP)
                        .subscribe(ignore -> view.dismissDropDown())
        );
    }

    public void onResume() {
        Optional<String> filter = this.filter;
        if (filter.isPresent()) {
            view.setQuery("", Collections.singletonList(filter.get()));
            view.expand(false);
            this.filter = Optional.absent();
        }
    }

    @Override
    public void onStop() {
        subscriptions.clear();
        drawerPresenter.onStop();
    }
}
