package com.github.st1hy.countthemcalories.activities.overview.graph.presenter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.graph.view.GraphColumn;
import com.github.st1hy.countthemcalories.activities.overview.graph.view.GraphColumnModel;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class GraphColumnViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.graph_item_name)
    TextView columnName;
    @BindView(R.id.graph_item_column)
    GraphColumn column;
    @Inject
    @Named("activityContext")
    Context context;
    @Inject
    @Named("graphItemClicked")
    PublishSubject<Integer> positionClicked;

    private final GraphColumnModel model;
    private float[] points = new float[8];
    private int position = -1;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public GraphColumnViewHolder(@NonNull @Named("columnRootView") View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        model = column.getModel();
    }

    @OnClick(R.id.graph_item_column)
    public void onClicked() {
        if (position != -1) positionClicked.onNext(position);
    }

    public void setName(@NonNull String name) {
        columnName.setText(name);
    }

    public void setValue(float value, float maxValue) {
        model.setValue(value, maxValue);
    }

    public void setWeight(float value, float min, float max) {
        model.setPoint(value, min, max);
    }

    public void setWeightVisibility(boolean isVisible) {
        setFlat(isVisible, GraphColumnModel.FLAG_POINTS);
    }

    public void setValueVisibility(boolean isVisible) {
        setFlat(isVisible, GraphColumnModel.FLAG_COLUMN);
    }

    private void setFlat(boolean isEnabled, @GraphColumnModel.VisibilityFlags int flag) {
        @GraphColumnModel.VisibilityFlags int flags = model.getFlags();
        if (isEnabled) {
            flags |= flag;
        } else {
            if ((flags & flag) > 0)
                flags -= flag;
        }
        model.setFlags(flags);
    }

    public float[] getMutable2SegmentLine() {
        return points;
    }

    public void setLine(float[] points) {
        model.setLinePoints(points);
    }

    public int getPos() {
        return position;
    }

    public void setPos(int position) {
        this.position = position;
    }

    public void setColor(@ColorInt int color) {
        model.setColumnColor(color);
    }

    public void onViewAttached(Observable<Integer> positionObservable) {
        subscriptions.add(
                positionObservable
                        .map(selectedPosition ->
                                getPos() == selectedPosition
                                        ? R.color.gcDefaultColorSelected
                                        : R.color.gcDefaultColor)
                        .map(colorRes -> ContextCompat.getColor(context, colorRes))
                        .subscribe(this::setColor)
        );
    }

    public void onViewDetached() {
        subscriptions.clear();
    }
}
