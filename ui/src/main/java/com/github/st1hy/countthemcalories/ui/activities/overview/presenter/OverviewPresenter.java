package com.github.st1hy.countthemcalories.ui.activities.overview.presenter;

import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import com.github.st1hy.countthemcalories.ui.activities.overview.addweight.AddWeightPresenter;
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.GraphFragment;
import com.github.st1hy.countthemcalories.ui.activities.overview.mealpager.MealsPagerPresenter;
import com.github.st1hy.countthemcalories.ui.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.ui.core.drawer.DrawerView;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;

import javax.inject.Inject;

@PerActivity
public class OverviewPresenter {

    @Inject
    GraphFragment graphFragment; //injects fragment dependencies
    @Inject
    DrawerPresenter drawerPresenter;
    @Inject
    ActionBarDrawerToggle drawerToggle;
    @Inject
    DrawerView drawerView;
    @Inject
    AddWeightPresenter addWeightPresenter;
    @Inject
    MealsPagerPresenter pagerPresenter;

    @Inject
    FabMenuDeactivation fabMenuDeactivation;

    @Inject
    public OverviewPresenter() {
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerPresenter.onClickedOnAction(item.getItemId());
    }

    public void onStart() {
        drawerPresenter.onStart();
        drawerView.registerToggle(drawerToggle);
        addWeightPresenter.onStart();
        pagerPresenter.onStart();
        fabMenuDeactivation.onStart();
    }

    public void onStop() {
        drawerPresenter.onStop();
        drawerView.unregisterDrawerToggle(drawerToggle);
        addWeightPresenter.onStop();
        pagerPresenter.onStop();
        fabMenuDeactivation.onStop();
    }
}
