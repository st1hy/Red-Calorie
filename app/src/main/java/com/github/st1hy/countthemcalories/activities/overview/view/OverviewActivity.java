package com.github.st1hy.countthemcalories.activities.overview.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.activities.overview.inject.DaggerOverviewActivityComponent;
import com.github.st1hy.countthemcalories.activities.overview.inject.OverviewActivityComponent;
import com.github.st1hy.countthemcalories.activities.overview.inject.OverviewActivityModule;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.drawer.DrawerView;

import javax.inject.Inject;

public class OverviewActivity extends BaseActivity {

    @Inject
    OverviewFragment content; //injects content fragment
    @Inject
    DrawerPresenter drawerPresenter;
    @Inject
    ActionBarDrawerToggle drawerToggle;
    @Inject
    DrawerView drawerView;

    OverviewActivityComponent component;

    @NonNull
    protected OverviewActivityComponent getComponent() {
        if (component == null) {
            component = DaggerOverviewActivityComponent.builder()
                    .applicationComponent(getAppComponent())
                    .overviewActivityModule(new OverviewActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);
        getComponent().inject(this);
        setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerPresenter.onClickedOnAction(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        drawerPresenter.onStart();
        drawerView.registerToggle(drawerToggle);
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawerPresenter.onStop();
        drawerView.unregisterDrawerToggle(drawerToggle);
    }


}
