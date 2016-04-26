package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.DaggerIngredientsActivityComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityModule;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.core.ui.BaseActivity;
import com.github.st1hy.countthemcalories.core.ui.Selection;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IngredientsActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        IngredientsView {
    public static final String ACTION_SELECT_INGREDIENT = "Select ingredient";

    @Inject
    IngredientsPresenter presenter;

    @Bind(R.id.ingredients_toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    SearchView searchView;

    IngredientsActivityComponent component;

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
        setContentView(R.layout.ingredients_activity);
        ButterKnife.bind(this);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        assertNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ingredient_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onClickedOnAction(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
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
    public void invokeActionBack() {
        super.onBackPressed();
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
    public void openOverviewScreen() {
        Intent intent = new Intent(this, OverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void setMenuItemSelection(@IdRes int nav_ingredients, @NonNull Selection selected) {
        navigationView.getMenu().findItem(nav_ingredients).setChecked(selected.is());
    }
}
