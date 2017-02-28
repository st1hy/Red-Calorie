package com.github.st1hy.countthemcalories.activities.overview.graph.presenter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnModule;
import com.github.st1hy.countthemcalories.activities.overview.model.DayData;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriod;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

@PerFragment
public class GraphDataAdapter extends RecyclerAdapterWrapper<GraphColumnViewHolder> {

    private static final int LAYOUT = R.layout.overview_graph_item;

    @Inject
    GraphColumnComponentFactory columnFactory;
    @Inject
    PhysicalQuantitiesModel quantityModel;
    @Inject
    SettingsModel settingsModel;

    TimePeriod timePeriod;

    @Inject
    public GraphDataAdapter() {
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
            holder.setValue(convertEnergy(day.getValue()), convertEnergy(2f * timePeriod.getMedian()));
            float normalizedWeight = getNormalizedWeight(day);
            holder.setWeight(normalizedWeight);
        }
    }

    private float convertEnergy(float energy) {
        return energy / settingsModel.getEnergyUnit().getBase().floatValue();
    }

    void onNewGraphData(@NonNull TimePeriod period) {
        this.timePeriod = period;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return timePeriod != null ? timePeriod.getDaysCount() : 0;
    }

    private float getNormalizedWeight(DayData day) {
        float minDisplayWeight = timePeriod.getMinWeight() * 0.98f;
        float maxDisplayWeight = timePeriod.getMaxWeight() * 1.01f;
        float weightDistance = maxDisplayWeight - minDisplayWeight;
        float normalizedWeight;
        if (weightDistance > 0f) {
            normalizedWeight = (day.getWeight() - minDisplayWeight) / weightDistance;
        } else {
            normalizedWeight = 0.5f;
        }
        return normalizedWeight;
    }

}
