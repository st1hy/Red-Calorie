package com.github.st1hy.countthemcalories.activities.overview.graph.presenter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnModule;
import com.github.st1hy.countthemcalories.activities.overview.graph.model.DayData;
import com.github.st1hy.countthemcalories.activities.overview.graph.model.GraphDataLoader;
import com.github.st1hy.countthemcalories.activities.overview.graph.model.GraphTimePeriod;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import org.joda.time.DateTime;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class GraphDataAdapter extends RecyclerAdapterWrapper<GraphColumnViewHolder> {

    private static final int LAYOUT = R.layout.overview_graph_item;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    GraphColumnComponentFactory columnFactory;
    @Inject
    GraphDataLoader data;
    @Inject
    PhysicalQuantitiesModel quantityModel;

    private GraphTimePeriod timePeriod;

    @Inject
    public GraphDataAdapter() {
    }

    public void onStart() {
        DateTime end = DateTime.now().plusDays(1);
        DateTime start = end.minusDays(30).withTimeAtStartOfDay();
        subscriptions.add(
            data.loadData(start, end)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onNewGraphData)
        );
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
            float value = day.getValue() / (2f * timePeriod.getMedian());
            if (value > 1f) value = 1f;
            holder.setValue(value);
        }
    }

    private void onNewGraphData(@NonNull GraphTimePeriod period) {
        this.timePeriod = period;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return timePeriod != null ? timePeriod.getCount() : 0;
    }
}
