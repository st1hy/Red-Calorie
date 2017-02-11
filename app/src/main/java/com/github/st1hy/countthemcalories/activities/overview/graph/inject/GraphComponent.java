package com.github.st1hy.countthemcalories.activities.overview.graph.inject;

import com.github.st1hy.countthemcalories.activities.overview.graph.GraphFragment;
import com.github.st1hy.countthemcalories.activities.overview.graph.inject.column.GraphColumnComponentFactory;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = GraphModule.class)
public interface GraphComponent extends GraphColumnComponentFactory {

    void inject(GraphFragment graphFragment);

}
