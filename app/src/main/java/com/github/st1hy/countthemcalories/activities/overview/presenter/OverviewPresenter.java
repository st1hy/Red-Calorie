package com.github.st1hy.countthemcalories.activities.overview.presenter;

import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import com.github.st1hy.countthemcalories.activities.overview.addweight.AddWeightPresenter;
import com.github.st1hy.countthemcalories.activities.overview.graph.GraphFragment;
import com.github.st1hy.countthemcalories.activities.overview.meals.MealsFragment;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.drawer.DrawerView;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Inject;

@PerActivity
public class OverviewPresenter {

    @Inject
    MealsFragment content; //injects content fragment
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
    public OverviewPresenter() {
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerPresenter.onClickedOnAction(item.getItemId());
    }

    public void onStart() {
        drawerPresenter.onStart();
        drawerView.registerToggle(drawerToggle);
        addWeightPresenter.onStart();
    }

    public void onStop() {
        drawerPresenter.onStop();
        drawerView.unregisterDrawerToggle(drawerToggle);
        addWeightPresenter.onStop();
    }
}
