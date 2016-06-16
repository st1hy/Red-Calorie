package com.github.st1hy.countthemcalories.core.tokensearch.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.tokensearch.model.SearchBarModel;
import com.github.st1hy.countthemcalories.core.tokensearch.view.SearchBarHolder;

import rx.subscriptions.CompositeSubscription;

public class SearchBarPresenter {

    private final SearchBarHolder viewHolder;
    private final SearchBarModel model;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public SearchBarPresenter(SearchBarHolder viewHolder, SearchBarModel model) {
        this.viewHolder = viewHolder;
        this.model = model;
    }

    public void onStart() {
        subscriptions.add(viewHolder.expandClicked()
                .subscribe(onExpandClicked()));
        subscriptions.add(viewHolder.collapseClicked()
                .subscribe(onCollapseClicked()));
        if (model.isExpanded()) {
            viewHolder.expand();
        } else {
            viewHolder.collapse();
        }
    }

    public void onStop() {
        subscriptions.clear();
    }

    public void onSaveInstanceState(@NonNull Bundle savedState) {
        model.onSaveState(savedState);
    }

    @NonNull
    private SimpleSubscriber<Void> onCollapseClicked() {
        return new SimpleSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                model.setExpanded(false);
                viewHolder.collapse();
            }
        };
    }

    @NonNull
    private SimpleSubscriber<Void> onExpandClicked() {
        return new SimpleSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                model.setExpanded(true);
                viewHolder.expand();
            }
        };
    }

}
