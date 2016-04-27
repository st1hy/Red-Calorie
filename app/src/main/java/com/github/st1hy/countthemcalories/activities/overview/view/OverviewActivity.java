package com.github.st1hy.countthemcalories.activities.overview.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.inject.DaggerOverviewActivityComponent;
import com.github.st1hy.countthemcalories.activities.overview.inject.OverviewActivityComponent;
import com.github.st1hy.countthemcalories.activities.overview.inject.OverviewActivityModule;
import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.core.ui.BaseActivity;
import com.github.st1hy.countthemcalories.core.ui.Selection;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OverviewActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        OverviewView {

    @Inject
    OverviewPresenter presenter;

    @Bind(R.id.overview_toolbar)
    Toolbar toolbar;
    @Bind(R.id.overview_fab)
    FloatingActionButton fab;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    ActionBarDrawerToggle drawerToggle;
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
        ButterKnife.bind(this);
        getComponent().inject(this);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAddMealButtonClicked();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.removeDrawerListener(drawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onClickedOnAction(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        presenter.onNavigationItemSelected(item.getItemId());
        return false;
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public boolean isDrawerOpen() {
        return drawer.isDrawerOpen(GravityCompat.START);
    }

    @Override
    public void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void invokeActionBack() {
        super.onBackPressed();
    }

    @Override
    public void openIngredientsScreen() {
        startActivity(new Intent(OverviewActivity.this, IngredientsActivity.class));
    }

    @Override
    public void setMenuItemSelection(@IdRes int menuId, @NonNull Selection selected) {
        navigationView.getMenu().findItem(menuId).setChecked(selected.is());
    }

    @Override
    public void openAddMealScreen() {
        startActivity(new Intent(this, AddMealActivity.class));
    }

    @Override
    public void openSettingsScreen() {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
