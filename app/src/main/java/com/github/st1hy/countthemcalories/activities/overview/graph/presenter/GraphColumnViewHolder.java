package com.github.st1hy.countthemcalories.activities.overview.graph.presenter;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.graph.view.GraphColumn;
import com.jakewharton.rxbinding.internal.Functions;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class GraphColumnViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.graph_item_name)
    TextView columnName;
    @BindView(R.id.graph_item_column)
    GraphColumn column;

    @Inject
    public GraphColumnViewHolder(@NonNull @Named("columnRootView") View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setName(@NonNull String name) {
        columnName.setText(name);
    }

    public void setValue(float value) {
        column.getModel().setProgress(value);
    }

    @NonNull
    @CheckResult
    public Observable<View> columnMargins() {
        return RxView.preDraws(column, Functions.FUNC0_ALWAYS_TRUE)
                .map(com.github.st1hy.countthemcalories.core.rx.Functions.into(column));
    }

    public void setWeight(float normalizedWeight) {
        column.getModel().setPoint(normalizedWeight);
    }
}
