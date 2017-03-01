package com.github.st1hy.countthemcalories.activities.overview.graph.presenter;

import android.support.v7.widget.RecyclerView;

import com.github.st1hy.countthemcalories.activities.overview.mealpager.PagerModel;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class GraphPresenter implements BasicLifecycle {

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    @Inject
    GraphDataAdapter graphDataAdapter;
    @Inject
    PagerModel model;
    @Inject
    RecyclerView recyclerView;

    @Inject
    public GraphPresenter() {
    }

    public void onStart() {
        subscriptions.add(
                Observable.just(model.getTimePeriod())
                        .filter(model -> model != null)
                        .mergeWith(
                                model.timePeriodChanges()
                        )
                        .subscribe(graphDataAdapter::onNewGraphData)
        );
        subscriptions.add(
                Observable.just(model.getSelectedPage())
                        .filter(page -> page >= 0)
                        .mergeWith(model.getSelectedPageChanges())
                        .distinctUntilChanged()
                        .doOnNext(recyclerView::scrollToPosition)
                        .subscribe(graphDataAdapter::setSelectedPosition)
        );
        subscriptions.add(
                graphDataAdapter.graphColumnClicked()
                        .subscribe(model::setSelectedPage)
        );
    }

    public void onStop() {
        subscriptions.clear();
    }
}
