package com.github.st1hy.countthemcalories;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.st1hy.countthemcalories.inject.component.DaggerIngredientsActivityComponent;
import com.github.st1hy.countthemcalories.inject.component.IngredientsActivityComponent;
import com.github.st1hy.countthemcalories.inject.module.IngredientsActivityModule;
import com.github.st1hy.countthemcalories.ui.BaseActivity;

import javax.inject.Inject;

public class IngredientsActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Inject
    Toolbar toolbar;
    @Inject
    DrawerLayout drawer;
    @Inject
    NavigationView navigationView;

    private IngredientsActivityComponent component;

    @NonNull
    protected IngredientsActivityComponent getComponent() {
        if (component == null) {
            component = DaggerIngredientsActivityComponent.builder()
                    .applicationComponent(getAppComponent())
                    .ingredientsActivityModule(new IngredientsActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        assertNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.getMenu().findItem(R.id.nav_ingredients).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_overview) {
            Intent intent = new Intent(this, OverviewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
