package com.github.st1hy.countthemcalories;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;

public class IngredientsActivity extends BaseDrawerActivity {

    @Bind(R.id.ingredients_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);
        bind();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.getMenu().findItem(R.id.nav_ingredients).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_overview) {
            onBackPressed();
        }
        return super.onNavigationItemSelected(item);
    }
}
