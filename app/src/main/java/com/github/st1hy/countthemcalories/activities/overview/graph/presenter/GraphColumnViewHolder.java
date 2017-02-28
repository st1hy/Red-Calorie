package com.github.st1hy.countthemcalories.activities.overview.graph.presenter;

import android.support.annotation.NonNull;
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

public class GraphColumnViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.graph_item_name)
    TextView columnName;
    @BindView(R.id.graph_item_column)
    GraphColumn column;
    private final GraphColumnModel model;

    @Inject
    public GraphColumnViewHolder(@NonNull @Named("columnRootView") View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        model = column.getModel();
    }

    public void setName(@NonNull String name) {
        columnName.setText(name);
    }

    public void setValue(float value, float maxValue) {
        model.setValue(value, maxValue);
    }

    public void setWeight(float normalizedWeight) {
        model.setPoint(normalizedWeight);
    }
}
