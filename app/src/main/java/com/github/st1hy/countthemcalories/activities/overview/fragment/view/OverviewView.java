package com.github.st1hy.countthemcalories.activities.overview.fragment.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.core.state.Visibility;

public interface OverviewView extends OverviewScreen {

    void setEmptyListVisibility(@NonNull Visibility visibility);

}
