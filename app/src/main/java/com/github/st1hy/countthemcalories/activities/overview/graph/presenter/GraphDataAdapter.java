package com.github.st1hy.countthemcalories.activities.overview.graph.presenter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnModule;
import com.github.st1hy.countthemcalories.activities.overview.graph.model.GraphGlobalViewModel;
import com.github.st1hy.countthemcalories.activities.overview.model.DayData;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriod;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

@PerFragment
public class GraphDataAdapter extends RecyclerAdapterWrapper<GraphColumnViewHolder> {

    private static final int LAYOUT = R.layout.overview_graph_item;

    @Inject
    GraphColumnComponentFactory columnFactory;
    @Inject
    PhysicalQuantitiesModel quantityModel;
    @Inject
    GraphGlobalViewModel graphGlobalViewModel;

    TimePeriod timePeriod;

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private boolean refreshGlobalMargin;

    @Inject
    public GraphDataAdapter() {
    }

    public void onStart() {

    }

    public void onStop() {
        subscriptions.clear();
    }

    @Override
    public GraphColumnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(LAYOUT, parent, false);
        return columnFactory.newColumn(new GraphColumnModule(view)).getHolder();
    }

    @Override
    public void onBindViewHolder(GraphColumnViewHolder holder, int position) {
        if (timePeriod != null) {
            DayData day = timePeriod.getDayDataAt(position);
            holder.setName(quantityModel.formatDate(day.getDateTime()));
            float value = timePeriod.normalizeDayValue(day);
            holder.setValue(value);
            refreshGlobalMargin(holder);
        }
    }

    void onNewGraphData(@NonNull TimePeriod period) {
        this.timePeriod = period;
        refreshGlobalMargin = true;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return timePeriod != null ? timePeriod.getDaysCount() : 0;
    }

    private void refreshGlobalMargin(GraphColumnViewHolder holder) {
        if (refreshGlobalMargin) {
            refreshGlobalMargin = false;
            subscriptions.add(
                    holder.columnMargins()
                            .first()
                            .subscribe(view -> graphGlobalViewModel.setMainAxisMargins(view.getLeft(), view.getBottom()))
            );
        }
    }
}
