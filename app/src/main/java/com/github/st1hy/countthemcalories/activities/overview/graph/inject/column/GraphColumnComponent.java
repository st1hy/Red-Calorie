package com.github.st1hy.countthemcalories.activities.overview.graph.inject.column;

import com.github.st1hy.countthemcalories.activities.overview.graph.presenter.GraphColumnViewHolder;

import dagger.Subcomponent;

@Subcomponent(modules = GraphColumnModule.class)
public interface GraphColumnComponent {

    GraphColumnViewHolder getHolder();
}
