package com.github.st1hy.countthemcalories.ui.activities.overview.graph.presenter;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.contract.model.DayStatistic;
import com.github.st1hy.countthemcalories.contract.model.CalorieStatistics;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject.column.GraphColumnComponentFactory;
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject.column.GraphColumnModule;
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.view.GraphColumnModel;
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.view.GraphColumnViewHolder;
import com.github.st1hy.countthemcalories.ui.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

@PerFragment
public class GraphDataAdapter extends RecyclerView.Adapter<GraphColumnViewHolder> {

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

    private CalorieStatistics statistics;

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
        if (statistics != null) {
            DayStatistic day = statistics.getDayDataAt(position);
            holder.setName(quantityModel.formatDate(day.getDateTime()));
            setCalories(holder, day);
            setWeight(holder, day);
            setLine(holder, position);
            holder.setPos(position);
            holder.setColor(defaultColorRes(position));
            holder.setBackground(defaultBackgroundRes(position));
        }
    }

    void onNewGraphData(@NonNull CalorieStatistics period) {
        this.statistics = period;
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
        return statistics != null ? statistics.getDaysCount() : 0;
    }

    private void setCalories(GraphColumnViewHolder holder, DayStatistic day) {
        float value = convertEnergy(day.getTotalCalories());
        float max = convertEnergy(2f * statistics.getMedian());
        holder.setValue(value, max);
        holder.setValueVisibility(value > 0);
    }

    private void setWeight(GraphColumnViewHolder holder, DayStatistic day) {
        float min = convertMass(minDisplayWeight(statistics.getMinWeight()));
        float max = convertMass(maxDisplayWeight(statistics.getMaxWeight()));
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
        return (float) (energy / settingsModel.getEnergyUnit().getBase());
    }

    private float convertMass(float mass) {
        return (float) (mass / settingsModel.getBodyMassUnit().getBase());
    }

    private void setLine(GraphColumnViewHolder holder, int position) {
        List<DayStatistic> days = statistics.getData();
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

    private float normalizeWeight(DayStatistic day) {
        float value = day.getWeight();
        if (value > 0) {
            float min = minDisplayWeight(statistics.getMinWeight());
            float max = maxDisplayWeight(statistics.getMaxWeight());
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
