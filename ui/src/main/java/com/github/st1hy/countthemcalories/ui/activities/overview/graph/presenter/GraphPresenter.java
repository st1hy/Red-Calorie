package com.github.st1hy.countthemcalories.ui.activities.overview.graph.presenter;

import android.support.v7.widget.RecyclerView;

import com.github.st1hy.countthemcalories.ui.activities.overview.mealpager.PagerModel;
import com.github.st1hy.countthemcalories.ui.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

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
        //noinspection Convert2MethodRef min api 24
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
                        .doOnNext(recyclerView::smoothScrollToPosition)
                        .subscribe(graphDataAdapter::setSelectedPosition)
        );
        subscriptions.add(
                graphDataAdapter.graphColumnClicked()
                        .subscribe(page -> model.setSelectedPage(page, true))
        );
    }

    public void onStop() {
        subscriptions.clear();
    }
}
