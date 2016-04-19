package com.github.st1hy.countthemcalories;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.st1hy.countthemcalories.inject.component.DaggerOverviewActivityComponent;
import com.github.st1hy.countthemcalories.inject.component.OverviewActivityComponent;
import com.github.st1hy.countthemcalories.inject.module.OverviewActivityModule;
import com.github.st1hy.countthemcalories.ui.BaseActivity;

import javax.inject.Inject;

public class OverviewActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Inject
    DrawerLayout drawer;
    @Inject
    NavigationView navigationView;
    @Inject
    Toolbar toolbar;
    @Inject
    FloatingActionButton fab;
    @Inject
    ActionBarDrawerToggle drawerToggle;

    private OverviewActivityComponent component;

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
        setContentView(R.layout.activity_overview);
        getComponent().inject(this);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.getMenu().findItem(R.id.nav_overview).setChecked(true);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //TODO
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_ingredients) {
            startActivity(new Intent(OverviewActivity.this, IngredientsActivity.class));
        }
        try {
            return true;
        } finally {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
