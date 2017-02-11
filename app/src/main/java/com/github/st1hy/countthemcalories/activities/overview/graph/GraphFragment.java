package com.github.st1hy.countthemcalories.activities.overview.graph;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.GraphComponentFactory;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;


public class GraphFragment extends BaseFragment {

    private GraphComponentFactory componentFactory;

    public void setComponentFactory(@NonNull GraphComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        componentFactory.newGraphComponent()
//                .inject(this);
        componentFactory = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.overview_graph_fragment, container, false);
    }
}
