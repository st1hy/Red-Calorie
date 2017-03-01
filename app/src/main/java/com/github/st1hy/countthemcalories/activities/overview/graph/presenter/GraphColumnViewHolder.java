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
}
