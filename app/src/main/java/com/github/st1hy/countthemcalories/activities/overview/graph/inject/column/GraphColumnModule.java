package com.github.st1hy.countthemcalories.activities.overview.graph.inject.column;

import android.view.View;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class GraphColumnModule {

    private final View rootView;

    public GraphColumnModule(View rootView) {
        this.rootView = rootView;
    }

    @Named("columnRootView")
    @Provides
    public View rootView() {
        return rootView;
    }
}
