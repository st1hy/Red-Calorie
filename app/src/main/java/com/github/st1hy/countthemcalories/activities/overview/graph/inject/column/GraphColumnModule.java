package com.github.st1hy.countthemcalories.activities.overview.graph.inject.column;

import android.view.View;

import dagger.Module;

@Module
public class GraphColumnModule {

    private final View rootView;

    public GraphColumnModule(View rootView) {
        this.rootView = rootView;
    }
}
