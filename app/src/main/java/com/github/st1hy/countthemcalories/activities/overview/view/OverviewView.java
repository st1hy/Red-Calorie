package com.github.st1hy.countthemcalories.activities.overview.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;
import com.github.st1hy.countthemcalories.core.state.Visibility;

import rx.Observable;

public interface OverviewView extends DrawerView {

    void openAddMealScreen();

    @NonNull
    Observable<Void> getOpenMealScreenObservable();

    void setTotalEnergy(@NonNull String energy);

    void setEmptyListVisibility(@NonNull Visibility visibility);

    void setEmptyListVariationVisibility(@NonNull Visibility visibility);

    Observable<Void> getDismissEmptyListVariationObservable();
}
