package com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject.column;

import android.view.View;

import dagger.Module;
import dagger.Provides;

@Module
public class GraphColumnModule {

    private final View rootView;

    public GraphColumnModule(View rootView) {
        this.rootView = rootView;
    }

    @GraphColumnRootView
    @Provides
    public View rootView() {
        return rootView;
    }
}
