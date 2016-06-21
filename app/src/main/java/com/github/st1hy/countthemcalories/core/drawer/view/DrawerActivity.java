package com.github.st1hy.countthemcalories.core.drawer.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.state.Selection;

import javax.inject.Inject;

import butterknife.BindView;

public abstract class DrawerActivity extends BaseActivity implements DrawerView, OnNavigationItemSelectedListener {

    @Inject
    DrawerPresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void showNavigationAsUp() {
        assertNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        presenter.onStop();
        drawer.removeDrawerListener(drawerToggle);
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
    public void openDrawerActivity(@NonNull DrawerMenuItem item) {
        Intent intent = new Intent(this, item.getActivityClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void setMenuItemSelection(@IdRes int menuId, @NonNull Selection selected) {
        navigationView.getMenu().findItem(menuId).setChecked(selected.is());
    }
}
