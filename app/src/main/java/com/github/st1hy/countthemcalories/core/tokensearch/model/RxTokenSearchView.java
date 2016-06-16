package com.github.st1hy.countthemcalories.core.tokensearch.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.tokensearch.view.SearchChanged;
import com.github.st1hy.countthemcalories.core.tokensearch.view.TokenSearchTextView;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class RxTokenSearchView implements Observable.OnSubscribe<SearchResult> {
    final TokenSearchTextView searchView;

    private RxTokenSearchView(@NonNull TokenSearchTextView searchView) {
        this.searchView = searchView;
    }

    @Override
    public void call(final Subscriber<? super SearchResult> subscriber) {
        searchView.setSearchChanged(new SearchChanged() {
            @Override
            public void onSearching(@NonNull String ingredientName, @NonNull List<String> tags) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(new SearchResult(ingredientName, tags));
                }
            }
        });
        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                searchView.setSearchChanged(null);
            }
        });
    }

    @NonNull
    public static Observable<SearchResult> create(@NonNull final TokenSearchTextView searchView) {
        return Observable.create(new RxTokenSearchView(searchView));
    }

}
