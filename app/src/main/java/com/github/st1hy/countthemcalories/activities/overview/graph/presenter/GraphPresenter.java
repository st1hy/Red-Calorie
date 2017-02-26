package com.github.st1hy.countthemcalories.activities.overview.graph.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.graph.model.GraphGlobalViewModel;
import com.github.st1hy.countthemcalories.activities.overview.graph.view.GraphLegend;
import com.github.st1hy.countthemcalories.activities.overview.mealpager.PagerModel;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.adapter.layoutmanager.HorizontalLayoutManager;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    GraphGlobalViewModel graphGlobalViewModel;
    @BindView(R.id.graph_front_legend)
    GraphLegend foregroundLegend;
    @BindView(R.id.graph_recycler)
    RecyclerView graphColumns;
    private boolean isLandscape;

    @Inject
    public GraphPresenter(@NonNull @Named("fragmentRoot") View fragmentRoot) {
        ButterKnife.bind(this, fragmentRoot);
    }

    public void onStart() {
        isLandscape = !(graphColumns.getLayoutManager() instanceof HorizontalLayoutManager);
        subscriptions.add(
                Observable.just(model.getTimePeriod())
                        .filter(model -> model != null && graphDataAdapter.timePeriod == null)
                        .mergeWith(
                                model.timePeriodChanges()
                        )
                        .doOnNext(timePeriod -> foregroundLegend.getModel().updateData(timePeriod))
                        .subscribe(graphDataAdapter::onNewGraphData)
        );
        subscriptions.add(
                graphGlobalViewModel.mainAxisMargins()
                        .subscribe(this::setForegroundLegendMargins)
        );
        graphDataAdapter.onStart();
    }

    private void setForegroundLegendMargins(@NonNull int[] margins) {
        MarginLayoutParams layoutParams = (MarginLayoutParams) foregroundLegend.getLayoutParams();
        ViewGroup parent = (ViewGroup) foregroundLegend.getParent();
        @Px int bottom = isLandscape ? 0 : parent.getHeight() - margins[1];
        layoutParams.setMargins(
                margins[0], layoutParams.topMargin, layoutParams.rightMargin, bottom
        );
        foregroundLegend.setLayoutParams(layoutParams);
    }

    public void onStop() {
        subscriptions.clear();
        graphDataAdapter.onStop();
    }
}
