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
            setCalories(holder, day);
            setWeight(holder, day);
        }
    }

    void onNewGraphData(@NonNull TimePeriod period) {
        this.timePeriod = period;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return timePeriod != null ? timePeriod.getDaysCount() : 0;
    }

    private void setCalories(GraphColumnViewHolder holder, DayData day) {
        float value = convertEnergy(day.getValue());
        float max = convertEnergy(2f * timePeriod.getMedian());
        holder.setValue(value, max);
        holder.setValueVisibility(value > 0);
    }

    private void setWeight(GraphColumnViewHolder holder, DayData day) {
        float min = convertMass(timePeriod.getMinWeight()) * 0.98f;
        float max = convertMass(timePeriod.getMaxWeight()) * 1.01f;
        float weight = convertMass(day.getWeight());
        float weightDistance = max - min;
        if (weightDistance > 0f && weight >= min && weight <= max) {
            holder.setWeight(weight, min, max);
            holder.setWeightVisibility(true);
        } else {
            holder.setWeight(0f, 0f, 1f);
            holder.setWeightVisibility(false);
        }
    }

    private float convertEnergy(float energy) {
        return energy / settingsModel.getEnergyUnit().getBase().floatValue();
    }

    private float convertMass(float mass) {
        return mass / settingsModel.getBodyMassUnit().getBase().floatValue();
    }

}
