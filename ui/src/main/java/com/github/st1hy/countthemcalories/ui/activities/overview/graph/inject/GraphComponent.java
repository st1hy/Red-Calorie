package com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject;

import com.github.st1hy.countthemcalories.ui.activities.overview.graph.GraphFragment;
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject.column.GraphColumnComponentFactory;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        GraphModule.class
})
public interface GraphComponent extends GraphColumnComponentFactory {

    void inject(GraphFragment graphFragment);

}
