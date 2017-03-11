package com.github.st1hy.countthemcalories.activities.overview.graph.presenter;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnModule;
import com.github.st1hy.countthemcalories.activities.overview.graph.view.GraphColumnModel;
import com.github.st1hy.countthemcalories.activities.overview.graph.view.GraphColumnViewHolder;
import com.github.st1hy.countthemcalories.activities.overview.model.DayData;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriod;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

@PerFragment
public class GraphDataAdapter extends RecyclerAdapterWrapper<GraphColumnViewHolder> {

    private static final int LAYOUT = R.layout.overview_graph_item;

    @Inject
    GraphColumnComponentFactory columnFactory;
    @Inject
    PhysicalQuantitiesModel quantityModel;
    @Inject
    SettingsModel settingsModel;
    @Inject
    @Named("graphItemClicked")
    PublishSubject<Integer> positionClicked;

    private TimePeriod timePeriod;

    private final BehaviorSubject<Integer> selectedPosition = BehaviorSubject.create();

    @Inject
    public GraphDataAdapter() {
    }

    public void setSelectedPosition(int position) {
        selectedPosition.onNext(position);
    }

    public Observable<Integer> graphColumnClicked() {
        return positionClicked;
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
            setLine(holder, position);
            holder.setPos(position);
            holder.setColor(defaultColorRes(position));
            holder.setBackground(defaultBackgroundRes(position));
        }
    }

    void onNewGraphData(@NonNull TimePeriod period) {
        this.timePeriod = period;
        notifyDataSetChanged();
    }

    @Override
    public void onViewAttachedToWindow(GraphColumnViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onViewAttached(selectedPosition);
    }

    @Override
    public void onViewDetachedFromWindow(GraphColumnViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onViewDetached();
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
        float min = convertMass(minDisplayWeight(timePeriod.getMinWeight()));
        float max = convertMass(maxDisplayWeight(timePeriod.getMaxWeight()));
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

    private void setLine(GraphColumnViewHolder holder, int position) {
        List<DayData> days = timePeriod.getData();
        float previous = position > 1 ? normalizeWeight(days.get(position - 1)) : -1f;
        float current = normalizeWeight(days.get(position));
        float next = position < days.size() - 1 ? normalizeWeight(days.get(position + 1)) : -1f;
        if (current > 0 && (previous > 0 || next > 0)) {
            float[] segmentLine = holder.getMutable2SegmentLine();
            if (previous > 0) {
                segmentLine[0] = 0;
                segmentLine[1] = (previous + current) / 2;
                segmentLine[2] = 0.5f;
                segmentLine[3] = current;
            } else {
                Arrays.fill(segmentLine, 0, 4, 0f);
            }
            if (next > 0) {
                segmentLine[4] = 0.5f;
                segmentLine[5] = current;
                segmentLine[6] = 1f;
                segmentLine[7] = (current + next) / 2;
            } else {
                Arrays.fill(segmentLine, 4, 8, 0f);
            }
            holder.setLine(segmentLine);
        } else {
            holder.setLine(GraphColumnModel.EMPTY_POINTS);
        }
    }

    private float normalizeWeight(DayData day) {
        float value = day.getWeight();
        if (value > 0) {
            float min = minDisplayWeight(timePeriod.getMinWeight());
            float max = maxDisplayWeight(timePeriod.getMaxWeight());
            float range = max - min;
            if (range > 0) return (value - min) / range;
        }
        return -1f;
    }

    private float minDisplayWeight(float value) {
        return value * 0.98f;
    }

    private float maxDisplayWeight(float value) {
        return value * 1.01f;
    }


    @ColorRes
    private int defaultColorRes(int position) {
        return position % 2 == 0 ? R.color.gcDefaultColor : R.color.gcDefaultColorAlternative;
    }

    @ColorRes
    private int defaultBackgroundRes(int position) {
        return position % 2 == 0 ? R.color.gcDefaultBackground : R.color.gcDefaultBackgroundAlternative;
    }

}
