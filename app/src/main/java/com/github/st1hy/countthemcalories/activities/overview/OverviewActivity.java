package com.github.st1hy.countthemcalories.activities.overview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.inject.activities.overview.OverviewActivityModule;
import com.github.st1hy.countthemcalories.inject.activities.overview.meals.OverviewFragmentComponentFactory;

import javax.inject.Inject;

public class OverviewActivity extends BaseActivity {

    @Inject
    OverviewPresenter presenter;
    @Inject
    OverviewFragmentComponentFactory mealsFragmentComponentFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);
        getAppComponent().newOverviewActivityComponent(new OverviewActivityModule(this))
                .inject(this);
        setTitle("");
        presenter.onCreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @NonNull
    public OverviewFragmentComponentFactory getMealsFragmentComponentFactory() {
        return mealsFragmentComponentFactory;
    }
}
