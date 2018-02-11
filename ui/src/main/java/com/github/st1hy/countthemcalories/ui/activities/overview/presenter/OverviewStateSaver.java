package com.github.st1hy.countthemcalories.ui.activities.overview.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.overview.mealpager.PagerModel;
import com.github.st1hy.countthemcalories.ui.core.WithState;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;

import org.parceler.Parcels;

import javax.inject.Inject;

@PerActivity
public class OverviewStateSaver implements WithState {

    public static final String SAVE_PAGE_STATE = "page state";

    @Inject
    PagerModel pagerModel;

    @Inject
    public OverviewStateSaver() {
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(SAVE_PAGE_STATE, Parcels.wrap(pagerModel));
    }
}
