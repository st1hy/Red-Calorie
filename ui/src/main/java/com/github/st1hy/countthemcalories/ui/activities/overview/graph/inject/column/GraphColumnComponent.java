package com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject.column;

import com.github.st1hy.countthemcalories.ui.activities.overview.graph.view.GraphColumnViewHolder;

import dagger.Subcomponent;

@Subcomponent(modules = GraphColumnModule.class)
public interface GraphColumnComponent {

    GraphColumnViewHolder getHolder();
}
