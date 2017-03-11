package com.github.st1hy.countthemcalories.activities.overview.graph.inject;

import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

public interface GraphComponentFactory {

    GraphComponent newGraphComponent(FragmentModule module);
}
