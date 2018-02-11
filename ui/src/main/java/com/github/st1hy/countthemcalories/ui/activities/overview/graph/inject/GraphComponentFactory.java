package com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject;

import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

public interface GraphComponentFactory {

    GraphComponent newGraphComponent(FragmentModule module);
}
