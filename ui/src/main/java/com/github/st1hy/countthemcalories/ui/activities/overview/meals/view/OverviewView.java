package com.github.st1hy.countthemcalories.ui.activities.overview.meals.view;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.ui.core.state.Visibility;

public interface OverviewView extends OverviewScreen {

    void setEmptyListVisibility(@NonNull Visibility visibility);

    void setEmptyBackground(@DrawableRes int drawableResId);
}
