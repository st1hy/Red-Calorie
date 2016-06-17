package com.github.st1hy.countthemcalories.core.tokensearch;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class RxSearchable implements Observable.OnSubscribe<SearchResult> {
    final Searchable searchable;

    private RxSearchable(@NonNull Searchable searchable) {
        this.searchable = searchable;
    }

    @Override
    public void call(final Subscriber<? super SearchResult> subscriber) {
        searchable.setOnSearchChanged(new OnSearchChanged() {
            @Override
            public void onSearching(@NonNull String ingredientName, @NonNull List<String> tags) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(newResults(ingredientName, tags));
                }
            }
        });
        subscriber.onNext(newResults(searchable.getQuery(), searchable.getTokens()));
        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                searchable.setOnSearchChanged(null);
            }
        });
    }

    @NonNull
    private static SearchResult newResults(@NonNull String query, @NonNull List<String> tokens) {
        return new SearchResult(query, tokens);
    }

    @NonNull
    public static Observable<SearchResult> create(@NonNull final Searchable searchView) {
        return Observable.create(new RxSearchable(searchView));
    }

}
