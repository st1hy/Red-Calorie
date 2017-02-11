package com.github.st1hy.countthemcalories.activities.overview.graph.presenter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnModule;
import com.github.st1hy.countthemcalories.activities.overview.graph.model.DayData;
import com.github.st1hy.countthemcalories.activities.overview.graph.model.GraphData;
import com.github.st1hy.countthemcalories.activities.overview.graph.model.GraphDayCursor;
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
    private GraphColumnComponentFactory columnFactory;
    @Inject
    private GraphData data;

    private GraphDayCursor cursor;

    @Inject
    public GraphDataAdapter() {
    }

    public void onStart() {
        DateTime end = DateTime.now();
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

    private void onNewGraphData(@NonNull GraphDayCursor cursor) {
        closeCursor();
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    private void closeCursor() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
            notifyDataSetChanged();
        }
    }

    @Override
    public GraphColumnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(LAYOUT, parent, false);
        return columnFactory.newColumn(new GraphColumnModule(view)).getHolder();
    }

    @Override
    public void onBindViewHolder(GraphColumnViewHolder holder, int position) {
        if (cursor != null) {
            DayData day = cursor.getDayDataAt(position);
            //TODO Continue writing data
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }
}
