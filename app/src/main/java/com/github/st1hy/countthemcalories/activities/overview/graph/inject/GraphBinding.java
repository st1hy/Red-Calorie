package com.github.st1hy.countthemcalories.activities.overview.graph.inject;

import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.graph.presenter.GraphDataAdapter;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class GraphBinding {

    public abstract GraphColumnComponentFactory columnFactory(GraphComponent component);

    @Binds
    public abstract RecyclerAdapterWrapper adapter(GraphDataAdapter adapter);
}
