package com.github.st1hy.countthemcalories.core.drawer;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.state.Selection;
import com.google.common.base.Preconditions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@PerActivity
public final class DrawerView {

    @NonNull
    private final AppCompatActivity activity;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Inject
    public DrawerView(@NonNull AppCompatActivity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
        activity.setSupportActionBar(toolbar);
    }

    public void setNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener listener) {
        navigationView.setNavigationItemSelectedListener(listener);
    }

    public void showNavigationAsUp() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
        Preconditions.checkNotNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    public void registerToggle(@NonNull ActionBarDrawerToggle drawerToggle) {
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    public void unregisterDrawerToggle(@NonNull ActionBarDrawerToggle drawerToggle) {
        drawer.removeDrawerListener(drawerToggle);
    }

    public boolean isDrawerOpen() {
        return drawer.isDrawerOpen(GravityCompat.START);
    }

    public void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    public void openDrawerActivity(@NonNull DrawerMenuItem item) {
        Intent intent = new Intent(activity, item.getActivityClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public void setMenuItemSelection(@IdRes int menuId, @NonNull Selection selected) {
        navigationView.getMenu().findItem(menuId).setChecked(selected.is());
    }

    public DrawerLayout getDrawer() {
        return drawer;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
