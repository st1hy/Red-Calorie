package com.github.st1hy.countthemcalories.activities.overview.graph.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.graph.GraphColumn;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GraphColumnViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.graph_item_name)
    TextView columnName;
    @BindView(R.id.graph_item_column)
    GraphColumn column;

    public GraphColumnViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


}
