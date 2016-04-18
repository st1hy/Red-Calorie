package com.github.st1hy.countthemcalories;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;

public class OverviewActivity extends BaseDrawerActivity {

    private ActionBarDrawerToggle drawerToggle;

    @Bind(R.id.overview_toolbar)
    Toolbar toolbar;
    @Bind(R.id.overview_fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        bind();
    }

    @Override
    protected void bind() {
        super.bind();
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        getMenuInflater().inflate(R.menu.overview, menu);
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
        return super.onNavigationItemSelected(item);
    }
}
