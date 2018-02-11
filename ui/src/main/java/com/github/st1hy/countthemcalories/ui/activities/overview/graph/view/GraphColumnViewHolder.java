package com.github.st1hy.countthemcalories.ui.activities.overview.graph.view;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject.column.GraphColumnRootView;
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.view.GraphColumnModel.VisibilityFlags;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext;

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
    @ActivityContext
    Context context;
    @Inject
    @Named("graphItemClicked")
    PublishSubject<Integer> positionClicked;

    private final GraphColumnModel model;
    private float[] points = new float[8];
    private int position = -1;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public GraphColumnViewHolder(@NonNull @GraphColumnRootView View itemView) {
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
        setFlag(isVisible, GraphColumnModel.FLAG_LINE);
        setFlag(isVisible, GraphColumnModel.FLAG_POINTS);
    }

    public void setValueVisibility(boolean isVisible) {
        setFlag(isVisible, GraphColumnModel.FLAG_COLUMN);
    }

    private void setFlag(boolean isEnabled, @VisibilityFlags int flag) {
        @VisibilityFlags int flags = model.getFlags();
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

    public void setPos(int position) {
        this.position = position;
    }

    public void setColor(@ColorRes int colorRes) {
        int color = ContextCompat.getColor(context, colorRes);
        model.setColumnColor(color);
    }

    public void setBackground(@ColorRes int colorRes) {
        int color = ContextCompat.getColor(context, colorRes);
        model.setBackgroundColor(color);
    }

    public void onViewAttached(Observable<Integer> positionObservable) {
        subscriptions.add(
                positionObservable.subscribe(this::onPositionSelected)
        );
    }

    public void onViewDetached() {
        subscriptions.clear();
    }

    private void onPositionSelected(int selectedPosition) {
        boolean isSelected = position == selectedPosition;
        model.setSelected(isSelected);
    }
}
