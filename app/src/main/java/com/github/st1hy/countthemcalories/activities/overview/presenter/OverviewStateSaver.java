package com.github.st1hy.countthemcalories.activities.overview.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.overview.mealpager.PagerModel;
import com.github.st1hy.countthemcalories.core.WithState;
import com.github.st1hy.countthemcalories.inject.PerActivity;

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
